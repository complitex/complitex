package ru.complitex.webapi;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:10 PM
 */
@Path("/webapi")
@ApplicationScoped
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
    public JsonObject getZerro(String nInfo) throws SQLException {
        JsonObject nInfoJson = Json.createReader(new StringReader(nInfo)).readObject();

        String accId = nInfoJson.getString("accId");

        if (accId == null || accId.isEmpty()){
            return null;
        }

        AccDebtToday accDebtToday = getAccDebtToday(accId);

        JsonObjectBuilder json =  Json.createObjectBuilder();

        json.add("accId", accId);

        if (accDebtToday.getDebt() != null && new BigDecimal(accDebtToday.getDebt()).compareTo(BigDecimal.ZERO) > 0){
            json.add("result", "0");

            json.add("conditions", Json.createArrayBuilder().add(Json.createObjectBuilder().add("saldo", accDebtToday.getDebt())));
        } else {
            json.add("result", "1");
        }

        return json.build();
    }
}
