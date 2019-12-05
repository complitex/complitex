package ru.complitex.webapi;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
    private DataSource dataSource;

    @Path("/getActualDebt")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getActualDebt(@QueryParam("acc") String acc) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            CallableStatement ps = connection.prepareCall(
                    "{? = call COMP.z$runtime_sz_utl.getAccDebtToday(?, ?, ?)}"
            );

            ps.registerOutParameter(1, Types.INTEGER);
            ps.setString(2, acc);
            ps.registerOutParameter(3, Types.VARCHAR);
            ps.registerOutParameter(4, Types.VARCHAR);

            ps.execute();

            return Json.createObjectBuilder()
                    .add("acc", ps.getString(3))
                    .add("debt", ps.getString(4))
                    .build();
        }
    }
}
