/*
 * Copyright (c) 2008 Objet Direct
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ru.complitex.common.web.component.wiquery.autocomplete;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.odlabs.wiquery.core.resources.JavaScriptHeaderItems;
import org.odlabs.wiquery.core.util.WiQueryUtil;

import java.util.List;

/**
 * $Id: AutocompleteJavascriptResourceReference.java 457 2010-10-15 07:14:28Z
 * hielke.hoeve@gmail.com $
 * <p>
 * References the JavaScript resource to get the Autocomplete component.
 * </p>
 * 
 * @author Julien Roche
 * @since 1.1
 */
public final class WiQueryAutocompleteJavaScriptResourceReference extends JavaScriptResourceReference
{
	private static final long serialVersionUID = 1L;

	/**
	 * Singleton instance.
	 */
	private static final WiQueryAutocompleteJavaScriptResourceReference INSTANCE =
		new WiQueryAutocompleteJavaScriptResourceReference();

	/**
	 * Builds a new instance of {@link WiQueryAutocompleteJavaScriptResourceReference}.
	 */
	private WiQueryAutocompleteJavaScriptResourceReference()
	{
		super(WiQueryAutocompleteJavaScriptResourceReference.class, "wiquery-autocomplete.js");
	}

	/**
	 * Returns the {@link WiQueryAutocompleteJavaScriptResourceReference} instance.
	 */
	public static WiQueryAutocompleteJavaScriptResourceReference get()
	{
		return INSTANCE;
	}

	@Override
	public List<HeaderItem> getDependencies()
	{
		return JavaScriptHeaderItems.forReferences(WiQueryUtil.getWicketEventReference(),
			WiQueryUtil.getWicketAjaxReference(), AutocompleteJavaScriptResourceReference.get());
	}
}
