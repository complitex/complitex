package ru.complitex.webapi;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:10 PM
 */
@Path("/webapi")
@RequestScoped
public class WebapiResource {
    private final static Logger log = Logger.getLogger(WebapiResource.class.getName());

    @Inject
    @Named("webapi")
    private DataSource dataSource;

    public static class AccDebtToday {
        private int result;
        private String acc;
        private String debt;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getAcc() {
            return acc;
        }

        public void setAcc(String acc) {
            this.acc = acc;
        }

        public String getDebt() {
            return debt;
        }

        public void setDebt(String debt) {
            this.debt = debt;
        }
    }

    @SuppressWarnings("SqlResolve")
    public AccDebtToday getAccDebtToday(String account)  throws SQLException{
        try (Connection connection = this.dataSource.getConnection()) {
            CallableStatement cs = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccDebtToday(?, ?, ?)}"
            );

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, account);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.registerOutParameter(4, Types.VARCHAR);

            cs.execute();

            AccDebtToday accDebtToday = new AccDebtToday();

            accDebtToday.setResult(cs.getInt(1));
            accDebtToday.setAcc(cs.getString(3));
            accDebtToday.setDebt(cs.getString(4));

            return accDebtToday;
        }
    }

    @Path("/getActualDebt")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getActualDebt(@QueryParam("acc") String account) throws SQLException {
        if (account == null || account.isEmpty()){
            return null;
        }

        AccDebtToday accDebtToday = getAccDebtToday(account);

        if (accDebtToday.getResult() == 0){
            return Json.createObjectBuilder()
                    .add("error_code", 0)
                    .add("error", "л/с не найден")
                    .build();
        }

        return Json.createObjectBuilder()
                .add("acc",  accDebtToday.getAcc() != null ? accDebtToday.getAcc() : "")
                .add("debt", accDebtToday.getDebt() != null ? accDebtToday.getDebt() : "")
                .build();
    }

    @Path("/getZerro")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject getZerro(String nInfo) throws NoSuchAlgorithmException {
        JsonObject nInfoJson = Json.createReader(new StringReader(nInfo)).readObject();

        String accId = nInfoJson.get("accId").toString().replace("\"", "");

        if (accId.isEmpty()){
            return null;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add("accId", accId);

        AccDebtToday accDebtToday;

        try {
            accDebtToday = getAccDebtToday(accId);

            if (accDebtToday.getResult() == 0){
                json.add("result", "0");

                json.add("error", "л/с не найден");
            } else if (accDebtToday.getDebt() != null && new BigDecimal(accDebtToday.getDebt()).compareTo(BigDecimal.ZERO) > 0){
                json.add("result", "0");

                json.add("conditions", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("saldo", accDebtToday.getDebt())));
            } else {
                json.add("result", "1");
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "getZerro error", e);

            json.add("result", "0");

            json.add("error", "неизвестная ошибка");
        }

        JsonObject zerro = json.build();

        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(zerro.toString().getBytes());

        byte[] digest = md.digest();

        return Json.createObjectBuilder(zerro)
                .add( "hash", DatatypeConverter.printHexBinary(digest))
                .build();
    }

    private JsonObject getResultObject(int resultCodeInfo){
        if (resultCodeInfo == 0){
            return Json.createObjectBuilder()
                    .add("error_code", 0)
                    .add("error", "л/с не найден")
                    .build();
        } else if (resultCodeInfo == -10){
            return Json.createObjectBuilder()
                    .add("error_code", -10)
                    .add("error", "Неопознанная ошибка")
                    .build();
        } else if (resultCodeInfo == -26){
            return Json.createObjectBuilder()
                    .add("error_code", -26)
                    .add("error", "Неизвестная локаль")
                    .build();
        }

        return null;
    }

    @Path("/getAccountInfo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("SqlResolve")
    public JsonObject getAccountInfo(@QueryParam("acc") String account, @QueryParam("date") String date,
                                     @QueryParam("locale") String locale) throws SQLException {
        if (account == null || account.isEmpty() || date == null || date.isEmpty()){
            return null;
        }

        LocalDate localDate = LocalDate.parse(date);

        try (Connection connection = this.dataSource.getConnection()) {
            JsonArrayBuilder info = Json.createArrayBuilder();

            {
                CallableStatement cs = connection.prepareCall(
                        "{? = call COMP.z$runtime_sz_utl.getAccInfo(?, ?, ?, ?)}"
                );

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, account);
                cs.setDate(3, Date.valueOf(localDate));

                cs.registerOutParameter(4, Types.REF_CURSOR);

                cs.setString(5, locale);

                cs.execute();

                JsonObject ro = getResultObject(cs.getInt(1));

                if (ro != null) {
                    return ro;
                }

                ResultSet rs = cs.getObject(4, ResultSet.class);

                while (rs.next()) {
                    info.add(Json.createObjectBuilder()
                            .add("street", rs.getString("STREET"))
                            .add("house", rs.getString("HOUSE"))
                            .add("flat", rs.getString("FLAT"))
                            .add("tarif", getString(rs.getBigDecimal("TARIF")))
                            .add("saldo", getString(rs.getBigDecimal("SALDO").setScale(2, HALF_EVEN)))
                            .add("charge", getString(rs.getBigDecimal("CHARGE").setScale(2, HALF_EVEN)))
                            .add("corr", getString(rs.getBigDecimal("CORR").setScale(2, HALF_EVEN)))
                            .add("pays", getString(rs.getBigDecimal("PAYS").setScale(2, HALF_EVEN)))
                            .add("to_pay", getString(rs.getBigDecimal("TOPAY").setScale(2, HALF_EVEN)))
                            .add("fio", rs.getString("FIO"))
                            .build()
                    );
                }
            }


            JsonArrayBuilder corr = Json.createArrayBuilder();

            {
                CallableStatement cs = connection.prepareCall(
                        "{? = call COMP.z$runtime_sz_utl.getAccSrvCorr(?, ?, ?, ?)}"
                );

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, account);
                cs.setDate(3, Date.valueOf(localDate));

                cs.registerOutParameter(4, Types.REF_CURSOR);

                cs.setString(5, locale);

                cs.execute();

                JsonObject ro = getResultObject(cs.getInt(1));

                if (ro != null) {
                    return ro;
                }

                ResultSet rs = cs.getObject(4, ResultSet.class);

                while (rs.next()) {
                    corr.add(Json.createObjectBuilder()
                            .add("srv", rs.getString("SRV"))
                            .add("corr", rs.getString("CORR"))
                            .build()
                    );
                }
            }

            JsonArrayBuilder charge = Json.createArrayBuilder();

            {
                CallableStatement cs = connection.prepareCall(
                        "{? = call COMP.z$runtime_sz_utl.getAccSrvCharge(?, ?, ?, ?)}"
                );

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, account);
                cs.setDate(3, Date.valueOf(localDate));

                cs.registerOutParameter(4, Types.REF_CURSOR);

                cs.setString(5, locale);

                cs.execute();

                JsonObject ro = getResultObject(cs.getInt(1));

                if (ro != null) {
                    return ro;
                }

                ResultSet rs = cs.getObject(4, ResultSet.class);

                while (rs.next()) {
                    charge.add(Json.createObjectBuilder()
                            .add("srv", rs.getString("SRV"))
                            .add("charge", rs.getString("CHARGE"))
                            .build()
                    );
                }
            }

            JsonArrayBuilder privs = Json.createArrayBuilder();

            {
                CallableStatement cs = connection.prepareCall(
                        "{? = call COMP.z$runtime_sz_utl.getPrivs(?, ?, ?, ?)}"
                );

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, account);
                cs.setDate(3, Date.valueOf(localDate));

                cs.registerOutParameter(4, Types.REF_CURSOR);

                cs.setString(5, locale);

                cs.execute();

                JsonObject ro = getResultObject(cs.getInt(1));

                if (ro != null) {
                    return ro;
                }

                ResultSet rs = cs.getObject(4, ResultSet.class);

                while (rs.next()) {
                    privs.add(Json.createObjectBuilder()
                            .add("code", rs.getString("CC"))
                            .add("quantity", rs.getString("UC"))
                            .add("date_in", getString(rs.getDate("DATE_IN")))
                            .add("date_out", getString(rs.getDate("DATE_OUT")))
                            .add("cat", rs.getString("CAT"))
                            .build()
                    );
                }
            }

            return Json.createObjectBuilder()
                    .add("acc_info", info.build())
                    .add("acc_srv_corr", corr.build())
                    .add("acc_srv_charge", charge.build())
                    .add("privs", privs.build())
                    .build();
        } catch (Exception e){
            log.log(Level.WARNING, "getAccountInfo error", e);

            return Json.createObjectBuilder()
                    .add("error", "неизвестная ошибка")
                    .build();
        }
    }

    @Path("/getAccountProv")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAccountProv(@QueryParam("acc") String account,
                                     @QueryParam("date-begin") String dateBegin,
                                     @QueryParam("date-end") String dateEnd) throws SQLException {
        if (account == null || account.isEmpty() || dateBegin == null || dateBegin.isEmpty() ||
                dateEnd == null || dateEnd.isEmpty()){
            return null;
        }

        LocalDate localDateBegin = LocalDate.parse(dateBegin);
        LocalDate localDateEnd = LocalDate.parse(dateEnd);

        JsonArrayBuilder prov = Json.createArrayBuilder();

        try (Connection connection = this.dataSource.getConnection()) {
            @SuppressWarnings("SqlResolve") CallableStatement cs = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccProv(?, ?, ?, ?)}"
            );

            cs.registerOutParameter(1, Types.INTEGER);

            cs.setString(2, account);
            cs.setDate(3, Date.valueOf(localDateBegin));
            cs.setDate(4, Date.valueOf(localDateEnd));

            cs.registerOutParameter(5, Types.REF_CURSOR);

            cs.execute();

            int resultCode = cs.getInt(1);

            if (resultCode == 0){
                return Json.createObjectBuilder()
                        .add("error_code", 0)
                        .add("error", "л/с не найден")
                        .build();
            }else if (resultCode == -10){
                return Json.createObjectBuilder()
                        .add("error_code", -10)
                        .add("error", "Неопознанная ошибка")
                        .build();
            }

            ResultSet rs = cs.getObject(5, ResultSet.class);

            while (rs.next()){
                prov.add(Json.createObjectBuilder()
                        .add("om", rs.getString("OM"))
                        .add("saldo", getString(rs.getBigDecimal("SALDO").setScale(2, HALF_EVEN)))
                        .add("charge", getString(rs.getBigDecimal("CHARGE").setScale(2, HALF_EVEN)))
                        .add("corr", getString(rs.getBigDecimal("CORR").setScale(2, HALF_EVEN)))
                        .add("pays", getString(rs.getBigDecimal("PAYS").setScale(2, HALF_EVEN)))
                        .add("priv", getString(rs.getBigDecimal("PRIV").setScale(2, HALF_EVEN)))
                        .add("subs", getString(rs.getBigDecimal("SUBS").setScale(2, HALF_EVEN)))
                        .build()
                );
            }
        } catch (Exception e){
            log.log(Level.WARNING, "getAccountProv error", e);

            return Json.createObjectBuilder()
                    .add("error", "неизвестная ошибка")
                    .build();
        }

        return Json.createObjectBuilder()
                .add("acc", account)
                .add("date-begin", getString(localDateBegin))
                .add("date-end", getString(localDateEnd))
                .add("prov", prov.build())
                .build();
    }

    private String getString(BigDecimal number){
        if (number != null){
            return number.toPlainString();
        }

        return null;
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private String getString(Date date){
        if (date != null){
            return dateFormat.format(date);
        }

        return null;
    }

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String getString(LocalDate localDate){
        if (localDate != null){
            return dateFormatter.format(localDate);
        }

        return null;
    }
}
