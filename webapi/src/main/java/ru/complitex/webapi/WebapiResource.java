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
import java.time.LocalDate;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:10 PM
 */
@Path("/webapi")
@RequestScoped
public class WebapiResource {
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

    public AccDebtToday getAccDebtToday(String account)  throws SQLException{
        try (Connection connection = this.dataSource.getConnection()) {
            @SuppressWarnings("SqlResolve") CallableStatement cs = connection.prepareCall(
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

    @Path("/getAccountInfo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAccountInfo(@QueryParam("acc") String account, @QueryParam("date") String date,
                                     @QueryParam("locale") String locale) throws SQLException {
        if (account == null || account.isEmpty() || date == null || date.isEmpty()){
            return null;
        }

        LocalDate localDate = LocalDate.parse(date);

        JsonArrayBuilder info = Json.createArrayBuilder();
        JsonArrayBuilder corr = Json.createArrayBuilder();

        try (Connection connection = this.dataSource.getConnection()) {
            @SuppressWarnings("SqlResolve") CallableStatement csInfo = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccInfo(?, ?, ?, ?)}"
            );

            csInfo.registerOutParameter(1, Types.INTEGER);
            csInfo.setString(2, account);
            csInfo.setDate(3, Date.valueOf(localDate));

            csInfo.registerOutParameter(4, Types.REF_CURSOR);

            csInfo.setString(5, locale);

            csInfo.execute();

            if (csInfo.getInt(1) != 1){
                return Json.createObjectBuilder().add("error_code", csInfo.getInt(1)).build();
            }

            ResultSet rsInfo = csInfo.getObject(4, ResultSet.class);

            while (rsInfo.next()){
                info.add(Json.createObjectBuilder()
                        .add("street", rsInfo.getString("STREET"))
                        .add("house", rsInfo.getString("HOUSE"))
                        .add("flat", rsInfo.getString("FLAT"))
                        .add("tarif", rsInfo.getBigDecimal("TARIF"))
                        .add("saldo", rsInfo.getBigDecimal("SALDO"))
                        .add("charge", rsInfo.getBigDecimal("CHARGE"))
                        .add("corr", rsInfo.getBigDecimal("CORR"))
                        .add("pays", rsInfo.getBigDecimal("PAYS"))
                        .add("to_pay", rsInfo.getBigDecimal("TOPAY"))
                        .add("fio", rsInfo.getString("FIO"))
                        .build()
                );
            }


            @SuppressWarnings("SqlResolve") CallableStatement psCorr = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccSrvCorr(?, ?, ?, ?)}"
            );

            psCorr.registerOutParameter(1, Types.INTEGER);
            psCorr.setString(2, account);
            psCorr.setDate(3, Date.valueOf(localDate));

            psCorr.registerOutParameter(4, Types.REF_CURSOR);

            psCorr.setString(5, locale);

            psCorr.execute();

            if (psCorr.getInt(1) != 1){
                return Json.createObjectBuilder().add("error_code", psCorr.getInt(1)).build();
            }

            ResultSet rsCorr = psCorr.getObject(4, ResultSet.class);

            while (rsCorr.next()){
                corr.add(Json.createObjectBuilder()
                        .add("srv", rsCorr.getString("SRV"))
                        .add("corr", rsCorr.getString("CORR")).build()
                );
            }
        }

        return Json.createObjectBuilder()
                .add("acc_info", info.build())
                .add("acc_srv_corr", corr.build())
                .build();
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

            if (cs.getInt(1) != 1){
                return Json.createObjectBuilder().add("error_code", cs.getInt(1)).build();
            }

            ResultSet rs = cs.getObject(5, ResultSet.class);

            while (rs.next()){
                prov.add(Json.createObjectBuilder()
                        .add("om", rs.getString("OM"))
                        .add("saldo", rs.getBigDecimal("SALDO"))
                        .add("charge", rs.getBigDecimal("CHARGE"))
                        .add("corr", rs.getBigDecimal("CORR"))
                        .add("pays", rs.getBigDecimal("PAYS"))
                        .add("priv", rs.getBigDecimal("PRIV"))
                        .add("subs", rs.getBigDecimal("SUBS"))
                        .build()
                );
            }
        }

        return Json.createObjectBuilder()
                .add("acc", account)
                .add("date-begin", dateBegin)
                .add("date-end", dateEnd)
                .add("prov", prov.build())
                .build();
    }
}
