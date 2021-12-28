package ru.complitex.pspoffice.address.api;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.Country;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class AddressJsonPage extends WebPage {
    @Inject
    private CatalogService catalogService;

    public AddressJsonPage(PageParameters parameters) {
        JSONArray jsonArray = new JSONArray();

        catalogService.getItems(Country.CATALOG, LocalDate.now()).get()
                .forEach(item -> {
                    JSONObject json = new JSONObject();

                    json.put("id", item.getId());

                    json.put("country_name_ru", item.getText(Country.COUNTRY_NAME, Locale.RU));

                    json.put("country_name_ua", item.getText(Country.COUNTRY_NAME, Locale.UA));

                    jsonArray.put(json);
                });

        getRequestCycle().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "utf-8", jsonArray.toString()));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType("html", "application/json");
    }
}
