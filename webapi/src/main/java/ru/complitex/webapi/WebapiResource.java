package ru.complitex.webapi;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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

    @Path("/getActualDebt")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getActualDebt(@QueryParam("acc") String account) throws SQLException {
        if (account == null || account.isEmpty()){
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            @SuppressWarnings("SqlResolve") CallableStatement ps = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccDebtToday(?, ?, ?)}"
            );

            ps.registerOutParameter(1, Types.INTEGER);
            ps.setString(2, account);
            ps.registerOutParameter(3, Types.VARCHAR);
            ps.registerOutParameter(4, Types.VARCHAR);

            ps.execute();

            String acc = ps.getString(3);
            String debt = ps.getString(4);

            return Json.createObjectBuilder()
                    .add("acc",  acc != null ? acc : "")
                    .add("debt", debt != null ? debt : "")
                    .build();
        }
    }

    @Path("/getZerro")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getZerro(@QueryParam("accId") String accId) throws SQLException {
        if (accId == null || accId.isEmpty()){
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            @SuppressWarnings("SqlResolve") CallableStatement ps = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccDebtToday(?, ?, ?)}"
            );

            ps.registerOutParameter(1, Types.INTEGER);
            ps.setString(2, accId);
            ps.registerOutParameter(3, Types.VARCHAR);
            ps.registerOutParameter(4, Types.VARCHAR);

            ps.execute();

            String acc = ps.getString(3);
            String debt = ps.getString(4);

            JsonObjectBuilder json =  Json.createObjectBuilder();

            json.add("accId", accId);

            if (debt != null && new BigDecimal(debt).compareTo(BigDecimal.ZERO) > 0){
                json.add("result", "0");

                json.add("conditions", Json.createArrayBuilder().add(Json.createObjectBuilder().add("saldo", debt)));
            } else {
                json.add("result", "1");
            }

            return json.build();
        }
    }
}
