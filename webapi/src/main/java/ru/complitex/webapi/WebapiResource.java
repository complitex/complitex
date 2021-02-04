package ru.complitex.webapi;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.*;
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
                                     @QueryParam("locale") String locale, Boolean lastOm) {
        if (account == null || account.isEmpty()){
            return null;
        }
        
        if (lastOm == null){
            lastOm = false;            
        }

        if (!lastOm && (date == null || date.isEmpty())){
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            JsonArrayBuilder info = Json.createArrayBuilder();

            Date om = !lastOm ? Date.valueOf(LocalDate.parse(date)) : null;

            {
                CallableStatement cs;
                ResultSet rs;

                if (!lastOm) {
                    cs = connection.prepareCall(
                            "{? = call COMP.z$runtime_sz_utl.getAccInfo(?, ?, ?, ?)}"
                    );

                    cs.registerOutParameter(1, Types.INTEGER);
                    cs.setString(2, account);
                    cs.setDate(3, om);
                    cs.registerOutParameter(4, Types.REF_CURSOR);
                    cs.setString(5, locale);
                    
                } else {
                    cs = connection.prepareCall(
                            "{? = call COMP.z$runtime_sz_utl.getLastAccInfo(?, ?, ?)}"
                    );

                    cs.registerOutParameter(1, Types.INTEGER);
                    cs.setString(2, account);
                    cs.registerOutParameter(3, Types.REF_CURSOR);
                    cs.setString(4, locale);
                }

                cs.execute();

                JsonObject ro = getResultObject(cs.getInt(1));

                if (ro != null) {
                    return ro;
                }

                if (!lastOm) {
                    rs = cs.getObject(4, ResultSet.class);
                } else {
                    rs = cs.getObject(3, ResultSet.class);
                }

                while (rs.next()) {
                    JsonObjectBuilder infoBuilder = Json.createObjectBuilder()
                            .add("street", rs.getString("STREET"))
                            .add("house", rs.getString("HOUSE"))
                            .add("flat", rs.getString("FLAT"))
                            .add("tarif", getValue(rs.getBigDecimal("TARIF")))
                            .add("saldo", getValue(rs.getBigDecimal("SALDO"), 2))
                            .add("charge", getValue(rs.getBigDecimal("CHARGE"), 2))
                            .add("corr", getValue(rs.getBigDecimal("CORR"), 2))
                            .add("pays", getValue(rs.getBigDecimal("PAYS"), 2))
                            .add("to_pay", getValue(rs.getBigDecimal("TOPAY"), 2))
                            .add("fio", rs.getString("FIO"))
                            .add("priv", getValue(rs.getBigDecimal("PRIV"), 2))
                            .add("subs", getValue(rs.getBigDecimal("SUBS"), 2));
                    
                    if (lastOm){
                        infoBuilder.add("om", rs.getString("OM"));

                        om = Date.valueOf(LocalDate.parse(rs.getString("OM"), dateTimeFormatter));
                    }

                    infoBuilder.add("ts", getValue(rs.getBigDecimal("TS"), 2));
                    
                    info.add(infoBuilder.build());
                }
            }


            JsonArrayBuilder corr = Json.createArrayBuilder();

            {
                CallableStatement cs = connection.prepareCall(
                        "{? = call COMP.z$runtime_sz_utl.getAccSrvCorr(?, ?, ?, ?)}"
                );

                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, account);
                cs.setDate(3, om);

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
                            .add("corr", getValue(rs.getBigDecimal("CORR").setScale(2, HALF_EVEN)))
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
                cs.setDate(3, om);

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
                            .add("charge", getValue(rs.getBigDecimal("CHARGE"), 2))
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
                cs.setDate(3, om);

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
                            .add("date_in", getValue(rs.getDate("DATE_IN")))
                            .add("date_out", getValue(rs.getDate("DATE_OUT")))
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

    @Path("/getLastAccountInfo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("SqlResolve")
    public JsonObject getLastAccountInfo(@QueryParam("acc") String account, @QueryParam("locale") String locale) {
        return getAccountInfo(account, null, locale, true);
    }

    @Path("/getAccountProv")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAccountProv(@QueryParam("acc") String account,
                                     @QueryParam("date-begin") String dateBegin,
                                     @QueryParam("date-end") String dateEnd) {
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
                        .add("saldo", getValue(rs.getBigDecimal("SALDO"), 2))
                        .add("charge", getValue(rs.getBigDecimal("CHARGE"), 2))
                        .add("corr", getValue(rs.getBigDecimal("CORR"), 2))
                        .add("pays", getValue(rs.getBigDecimal("PAYS"), 2))
                        .add("priv", getValue(rs.getBigDecimal("PRIV"), 2))
                        .add("subs", getValue(rs.getBigDecimal("SUBS"), 2))
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
                .add("date-begin", getValue(localDateBegin))
                .add("date-end", getValue(localDateEnd))
                .add("prov", prov.build())
                .build();
    }

    private JsonValue getValue(BigDecimal number, int scale){
        if (number != null){
            return Json.createValue(number.setScale(scale, HALF_EVEN).toPlainString());
        }

        return JsonValue.NULL;
    }

    private JsonValue getValue(BigDecimal number){
        if (number != null){
            return Json.createValue(number.toPlainString());
        }

        return JsonValue.NULL;
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private JsonValue getValue(Date date){
        if (date != null){
            return Json.createValue(dateFormat.format(date));
        }

        return JsonValue.NULL;
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JsonValue getValue(LocalDate localDate){
        if (localDate != null){
            return Json.createValue(dateTimeFormatter.format(localDate));
        }

        return JsonValue.NULL;
    }
}
