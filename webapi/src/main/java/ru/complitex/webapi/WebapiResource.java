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
            @SuppressWarnings("SqlResolve") CallableStatement ps = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccDebtToday(?, ?, ?)}"
            );

            ps.registerOutParameter(1, Types.INTEGER);
            ps.setString(2, account);
            ps.registerOutParameter(3, Types.VARCHAR);
            ps.registerOutParameter(4, Types.VARCHAR);

            ps.execute();

            AccDebtToday accDebtToday = new AccDebtToday();

            accDebtToday.setResult(ps.getInt(1));
            accDebtToday.setAcc(ps.getString(3));
            accDebtToday.setDebt(ps.getString(4));

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
    public JsonObject getZerro(String nInfo) throws SQLException, NoSuchAlgorithmException {
        JsonObject nInfoJson = Json.createReader(new StringReader(nInfo)).readObject();

        String accId = nInfoJson.get("accId").toString();

        if (accId == null || accId.isEmpty()){
            return null;
        }

        AccDebtToday accDebtToday = getAccDebtToday(accId);

        JsonObjectBuilder json = Json.createObjectBuilder();

        json.add("accId", accId);

        if (accDebtToday.getDebt() != null && new BigDecimal(accDebtToday.getDebt()).compareTo(BigDecimal.ZERO) > 0){
            json.add("result", "0");

            json.add("conditions", Json.createArrayBuilder().add(Json.createObjectBuilder().add("saldo", accDebtToday.getDebt())));
        } else {
            json.add("result", "1");
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
            @SuppressWarnings("SqlResolve") CallableStatement psInfo = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccInfo(?, ?, ?, ?)}"
            );

            psInfo.registerOutParameter(1, Types.INTEGER);
            psInfo.setString(2, account);
            psInfo.setDate(3, Date.valueOf(localDate));

            psInfo.registerOutParameter(4, Types.REF_CURSOR);

            psInfo.setString(5, locale);

            psInfo.execute();

            if (psInfo.getInt(1) != 1){
                return Json.createObjectBuilder().add("error_code", psInfo.getInt(1)).build();
            }

            ResultSet rsInfo = psInfo.getObject(4, ResultSet.class);

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
                        .add("to_pay", rsInfo.getBigDecimal("TOPAY")).build()
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
}
