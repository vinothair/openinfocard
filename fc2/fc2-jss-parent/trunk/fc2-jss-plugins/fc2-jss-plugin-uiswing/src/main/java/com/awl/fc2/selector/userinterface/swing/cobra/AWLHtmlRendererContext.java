/*
GNU LESSER GENERAL PUBLIC LICENSE
Copyright (C) 2006 The Lobo Project

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

Contact info: lobochief@users.sourceforge.net
*/
/*
* Created on Oct 22, 2005
*/

package com.awl.fc2.selector.userinterface.swing.cobra;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.lobobrowser.html.*;
import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleBrowserFrame;
import org.lobobrowser.util.io.*;
import org.lobobrowser.util.*;
import org.w3c.dom.html2.*;

import com.awl.fc2.selector.userinterface.swing.Dialog_ModalNotif;

import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* The <code>AWLHtmlRendererContext</code> class implements
* the {@link org.lobobrowser.html.HtmlRendererContext} interface.
* Note that this class provides rudimentary implementations
* of most callback methods. Overridding some of the methods
* in this class will usually be necessary in a professional application.
* <p>
* A simple way to load a URL into the {@link HtmlPanel} of the
* renderer context is to invoke {@link #navigate(String)}. 
*/
public class AWLHtmlRendererContext implements HtmlRendererContext {
	private static final Logger logger = Logger.getLogger(AWLHtmlRendererContext.class.getName());
	
	private final HtmlPanel htmlPanel;
	private final HtmlRendererContext parentRcontext;
	
	/**
	 * Constructs a AWLHtmlRendererContext.
	 * @param contextComponent The component that will render HTML.
	 * @deprecated Use constructor that takes <code>HtmlPanel</code> and <code>UserAgentContext</code>
	 */
	public AWLHtmlRendererContext(HtmlPanel contextComponent) {
		this(contextComponent, (UserAgentContext) null);
	}
	
	/**
	 * Constructs a AWLHtmlRendererContext.
	 * @param contextComponent The component that will render HTML.
	 * @see SimpleUserAgentContext
	 */
	public AWLHtmlRendererContext(HtmlPanel contextComponent, UserAgentContext ucontext) {
		super();
		this.htmlPanel = contextComponent;
		this.parentRcontext = null;
		this.bcontext = ucontext;
	}
	
	/**
	 * Constructs a AWLHtmlRendererContext that is a child of another
	 * <code>{@link HtmlRendererContext}</code>.
	 * @param contextComponent The component that will render HTML.
	 * @param parentRcontext The parent's renderer context. 
	 */
	public AWLHtmlRendererContext(HtmlPanel contextComponent, HtmlRendererContext parentRcontext) {
		super();
		this.htmlPanel = contextComponent;
		this.parentRcontext = parentRcontext;
		this.bcontext = parentRcontext == null ? null : parentRcontext.getUserAgentContext();
	}
	
	public HtmlPanel getHtmlPanel() {
	    return this.htmlPanel;
	}
	
	private volatile String sourceCode;
	
	/**
	 * Gets the source code of the current HTML document.
	 */
	public String getSourceCode() {
		return this.sourceCode;
	}
	
	/**
	 * Gets a collection of current document frames, by querying
	 * the document currently held by the local 
	 * {@link org.lobobrowser.html.gui.HtmlPanel}
	 * instance.
	 */
	public HTMLCollection getFrames() {
		Object rootNode = this.htmlPanel.getRootNode();
		if(rootNode instanceof HTMLDocumentImpl) {
			return ((HTMLDocumentImpl) rootNode).getFrames();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Implements reload as navigation to current URL.
	 * Override to implement a more robust reloading
	 * mechanism.
	 */
	public void reload() {
		HTMLDocumentImpl document = (HTMLDocumentImpl) this.htmlPanel.getRootNode();
		if(document != null) {
			try {
				URL url = new URL(document.getDocumentURI());
				this.navigate(url, null);
			} catch(java.net.MalformedURLException throwable) {
				this.warn("reload(): Malformed URL", throwable);
			}
		}
	}
	
	/**
	 * Implements the link click handler by invoking {@link #navigate(URL, String)}.
	 */
	public void linkClicked(HTMLElement linkNode, URL url, String target) {
		this.navigate(url, target);
	}
	
	/**
	 * Gets the connection proxy used in {@link #navigate(URL, String)}.
	 * This implementation calls {@link SimpleUserAgentContext#getProxy()}
	 * if {@link #getUserAgentContext()} returns an instance assignable to {@link SimpleUserAgentContext}.
	 * The method may be overridden to provide a different proxy setting.
	 */
	protected Proxy getProxy() {
		Object ucontext = this.getUserAgentContext();
		if(ucontext instanceof AWLUserAgentContext) {
			return ((AWLUserAgentContext) ucontext).getProxy();
		}
		return Proxy.NO_PROXY;
	}
	
	/**
	 * Implements simple navigation with incremental
	 * rendering by invoking {@link #submitForm(String, URL, String, String, FormInput[])}
	 * with a <code>GET</code> request method.
	 */
	public void navigate(final URL href, String target) {
		this.submitForm("GET", href, target, null, null);

	}
	
	/**
	 * Convenience method provided to allow loading a document into
	 * the renderer.
	 * @param fullURL The absolute URL of the document.
	 * @see #navigate(URL, String)
	 */
	public void navigate(String fullURL) throws java.net.MalformedURLException {
		java.net.URL href = Urls.createURL(null, fullURL);
		this.navigate(href, "_this");
	}
	
	/**
	 * Implements simple navigation and form submission with incremental
	 * rendering and target processing, including
	 * frame lookup. Should be overridden to allow for
	 * more robust browser navigation and form submission.
	 * <p>
	 * <b>Notes:</b>
	 * <ul>
	 * <li>Document encoding is defined by {@link #getDocumentCharset(URLConnection)}.
	 * <li>Caching is not implemented.
	 * <li>Cookies are not implemented.
	 * <li>Incremental rendering is not optimized for
	 *     ignorable document change notifications.
	 * <li>Other HTTP features are not implemented.
	 * <li>The only form encoding type supported is <code>application/x-www-form-urlencoded</code>.
	 * <li>Navigation is normally asynchronous. See {@link #isNavigationAsynchronous()}.
	 * </ul>
	 * @see #navigate(URL, String)
	 */
	public void submitForm(final String method, final java.net.URL action, final String target, final String enctype, final FormInput[] formInputs) {
		
		BrowserWindow.getInstance().getAddressField().setText(action.toString());
		
		//In case an html page is loaded from a POST request
		if(action.getFile().endsWith(".html") && method.equalsIgnoreCase("POST")) {
			
			BufferedReader reader;
			String content = "";
			try {
				reader = new BufferedReader(new InputStreamReader(action.openStream()));

			
			String line;
			
			//Retrieve the html content
			while ((line = reader.readLine()) != null) {
				content+=line;
			}
			System.out.println(content);
			reader.close();
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			//Try to identify a pattern in the html file (authentication ticket hidden in the header).
			Pattern p = Pattern.compile("\\Q<authentication>\\E.*\\Q</authentication>\\E");
			Matcher m;
			System.out.println(action);
			m = p.matcher(content);

			//If the pattern is found, do whatever you like with it.
			if(m.find()) {
//				JLabel match = new JLabel(m.group());
//				match.setBounds(20,722,911,28);
//				BrowserWindow.getInstance().getContentPane().add(match);
				System.out.println(m.group());
			}
		}
		
		
		// This method implements simple incremental rendering.
		if(target != null) {
			HtmlRendererContext topCtx = this.getTop();
			HTMLCollection frames = topCtx.getFrames();
			if(frames != null) {
				org.w3c.dom.Node frame = frames.namedItem(target);
				if(logger.isLoggable(Level.INFO)) {
					logger.info("submitForm(): Frame matching target=" + target + " is " + frame);
				} 
				if(frame instanceof FrameNode) {
					BrowserFrame bframe = ((FrameNode) frame).getBrowserFrame();
					if(bframe == null) {
						throw new IllegalStateException("Frame node without a BrowserFrame instance: " + frame);
					}
					if(bframe.getHtmlRendererContext() != this) {
						bframe.loadURL(action);
						return;	
					}
				}
			}
			String actualTarget = target.trim().toLowerCase();
			if("_top".equals(actualTarget)) {
				this.getTop().navigate(action, null);
				return;
			}
			else if ("_parent".equals(actualTarget)) {
				HtmlRendererContext parent = this.getParent();
				if(parent != null) {
					parent.navigate(action, null);
					return;
				}
			}
			else if("_blank".equals(actualTarget)) {
				this.open(action, "cobra.blank", "", false);
				return;
			}
			else if("_this".equals(actualTarget)) {
				// fall through
			}
			else {
				logger.warning("submitForm(): Link target unrecognized: " + actualTarget);
			}
		}
		
		// Make request asynchronously.
		if(this.isNavigationAsynchronous()) {
			new Thread() {
				public void run() {
					try {
						AWLHtmlRendererContext.this.submitFormSync(method, action, target, enctype, formInputs);
					} catch (Exception err) {
						AWLHtmlRendererContext.this.error(
								"navigate(): Error loading or parsing request.",
								err);
					}
				}
			}.start();
		}
		else {
			try {
				AWLHtmlRendererContext.this.submitFormSync(method, action, target, enctype, formInputs);
			} catch (Exception err) {
				AWLHtmlRendererContext.this.error(
						"navigate(): Error loading or parsing request.",
						err);
			}			
		}
	}
	
	/**
	 * Indicates whether navigation (via {@link #submitForm(String, URL, String, String, FormInput[])}) should be asynchronous.
	 * This overridable implementation returns <code>true</code>.
	 */
	protected boolean isNavigationAsynchronous() {
		return true;
	}
	
	/**
	 * The connection currently opened by openSync() if any.
	 */
	protected URLConnection currentConnection;
	
	/**
	 * Submits a form and/or navigates by making
	 * a <i>synchronous</i> request. This method is invoked
	 * by {@link #submitForm(String, URL, String, String, FormInput[])}.
	 * @param method The request method.
	 * @param action The action URL.
	 * @param target The target identifier.
	 * @param enctype The encoding type.
	 * @param formInputs The form inputs.
	 * @throws IOException
	 * @throws org.xml.sax.SAXException
	 * @see #submitForm(String, URL, String, String, FormInput[])
	 */
	protected void submitFormSync(final String method, final java.net.URL action, final String target, String enctype, final FormInput[] formInputs) throws IOException, org.xml.sax.SAXException {
		final String actualMethod = method.toUpperCase();
		URL resolvedURL;
		if("GET".equals(actualMethod) && formInputs != null) {
			boolean firstParam = true;
			//TODO: What about the userInfo part of the URL?
			URL noRefAction = new URL(action.getProtocol(), action.getHost(), action.getPort(), action.getFile());
			StringBuffer newUrlBuffer = new StringBuffer(noRefAction.toExternalForm());
			if(action.getQuery() == null) {
				newUrlBuffer.append("?");
			}
			else {
				newUrlBuffer.append("&");
			}
			for(int i = 0; i < formInputs.length; i++) {
				FormInput parameter = formInputs[i];
				String name = parameter.getName();
				String encName = URLEncoder.encode(name, "UTF-8");
				if(parameter.isText()) {
					if(firstParam) {
						firstParam = false;
					}
					else {
						newUrlBuffer.append("&");
					}
					String valueStr = parameter.getTextValue();
					String encValue = URLEncoder.encode(valueStr, "UTF-8");
					newUrlBuffer.append(encName);
					newUrlBuffer.append("=");
					newUrlBuffer.append(encValue);
				}
				else {
					logger.warning("postData(): Ignoring non-textual parameter " + name + " for GET.");
				}	
			}	
			resolvedURL = new java.net.URL(newUrlBuffer.toString());
		}
		else {
			resolvedURL = action;
		}
		URL urlForLoading;
		if(resolvedURL.getProtocol().equalsIgnoreCase("file")) {
			// Remove query so it works.
			try {
				String ref = action.getRef();
				String refText = ref == null || ref.length() == 0 ? "" : "#" + ref;
				urlForLoading = new URL(resolvedURL.getProtocol(), action.getHost(), action.getPort(), action.getPath() + refText);
			} catch(java.net.MalformedURLException throwable) {
				this.warn("malformed", throwable);
				urlForLoading = action;
			}
		}
		else {
			urlForLoading = resolvedURL;
		}
		if(logger.isLoggable(Level.INFO)) {
			logger.info("process(): Loading URI=[" + urlForLoading + "].");
		}
		long time0 = System.currentTimeMillis();
		// Using potentially different URL for loading.
		Proxy proxy = AWLHtmlRendererContext.this.getProxy();
		boolean isPost = "POST".equals(actualMethod);
		URLConnection connection = proxy == null || proxy == Proxy.NO_PROXY ? urlForLoading.openConnection() : urlForLoading.openConnection(proxy);
		this.currentConnection = connection;
		try {
			connection.setRequestProperty("User-Agent", getUserAgentContext().getUserAgent());
			connection.setRequestProperty("Cookie", "");
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection hc = (HttpURLConnection) connection;
				hc.setRequestMethod(actualMethod);
				hc.setInstanceFollowRedirects(false);
			}
			if(isPost) {
				connection.setDoOutput(true);
				ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
				boolean firstParam = true;
				if(formInputs != null) {
					for(int i = 0; i < formInputs.length; i++) {
						FormInput parameter = formInputs[i];
						String name = parameter.getName();
						String encName = URLEncoder.encode(name, "UTF-8");
						if(parameter.isText()) {
							if(firstParam) {
								firstParam = false;
							}
							else {
								bufOut.write((byte) '&');				    					
							}
							String valueStr = parameter.getTextValue();
							String encValue = URLEncoder.encode(valueStr, "UTF-8");
							bufOut.write(encName.getBytes("UTF-8"));
							bufOut.write((byte) '=');
							bufOut.write(encValue.getBytes("UTF-8"));
						}
						else {
							logger.warning("postData(): Ignoring non-textual parameter " + name + " for POST.");
						}
					}
				}
				// Do not add a line break to post content. Some servers
				// can be picky about that (namely, java.net).
				byte[] postContent = bufOut.toByteArray();
				if(connection instanceof HttpURLConnection) {
					((HttpURLConnection) connection).setFixedLengthStreamingMode(postContent.length);
				}
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				//connection.setRequestProperty("Content-Length", String.valueOf(postContent.length));
				OutputStream postOut = connection.getOutputStream();
				postOut.write(postContent);
				postOut.flush();
			}					
			if (connection instanceof HttpURLConnection) {					
				HttpURLConnection hc = (HttpURLConnection) connection;
				int responseCode = hc.getResponseCode();
				if(logger.isLoggable(Level.INFO)) {
					logger.info("process(): HTTP response code: "
							+ responseCode);
				}
				if(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
					String location = hc.getHeaderField("Location");
					if(location == null) {
						logger.warning("No Location header in redirect from " + action + ".");
					}
					else {
						java.net.URL href;
						href = Urls.createURL(action, location);
						AWLHtmlRendererContext.this.navigate(href, target);
					}
					return;
				}
			}
			InputStream in = connection.getInputStream();
			try {
				AWLHtmlRendererContext.this.sourceCode = null;
				long time1 = System.currentTimeMillis();
				RecordedInputStream rin = new RecordedInputStream(in, 1000000);
				InputStream bin = new BufferedInputStream(rin, 8192);
				String actualURI = urlForLoading.toExternalForm();
				// Only create document, don't parse.
				HTMLDocumentImpl document = this.createDocument(new InputSourceImpl(bin, actualURI, getDocumentCharset(connection)));
				// Set document in HtmlPanel. Safe to call outside GUI thread.
				HtmlPanel panel = htmlPanel; 
				panel.setDocument(document, AWLHtmlRendererContext.this);
				// Now start loading.
				document.load();
				long time2 = System.currentTimeMillis();
				if(logger.isLoggable(Level.INFO)) {
					logger.info("Parsed URI=[" + urlForLoading + "]: Parse elapsed: "
							+ (time2 - time1) + " ms. Connection elapsed: "
							+ (time1 - time0) + " ms.");
				}
				String ref = urlForLoading.getRef();
				if(ref != null && ref.length() != 0) {
					panel.scrollToElement(ref);
				}
				try {
					AWLHtmlRendererContext.this.sourceCode = rin.getString("ISO-8859-1");
				} catch(BufferExceededException bee) {
					AWLHtmlRendererContext.this.sourceCode = "[TOO BIG]";
				}
			} finally { 
				in.close();
			}
		} finally {
			this.currentConnection = null;
		}
	}
	
	/**
	 * Creates a blank document instance. This method
	 * is invoked whenever navigation or form submission
	 * occur. It is provided so it can be overridden
	 * to create specialized document implmentations.
	 * @param inputSource The document input source.
	 * @throws IOException
	 * @throws org.xml.sax.SAXException
	 */
	protected HTMLDocumentImpl createDocument(org.xml.sax.InputSource inputSource) throws IOException, org.xml.sax.SAXException {
		DocumentBuilderImpl builder = new DocumentBuilderImpl(this.getUserAgentContext(), AWLHtmlRendererContext.this);
		return (HTMLDocumentImpl) builder.createDocument(inputSource);
	}
	/**
	 * This method is invoked by {@link #submitForm(String, URL, String, String, FormInput[])}
	 * to determine the charset of a document. The charset is determined by looking
	 * at the <code>Content-Type</code> header.
	 * @param connection A URL connection.
	 */
	protected String getDocumentCharset(URLConnection connection) {
		String encoding = Urls.getCharset(connection);
		return encoding == null ? "ISO-8859-1" : encoding;
	}
	
	// Methods useful to Window below:
	
	/**
	 * Opens a simple message dialog.
	 */
	public void alert(String message) {
		JOptionPane.showMessageDialog(this.htmlPanel, message);
	}
	
	/**
	 * It should give up focus on the current browser window. This implementation does nothing
	 * and should be overridden.
	 */
	public void blur() {
		this.warn("back(): Not overridden");
	}
	
	/**
	 * It should close the current browser window. This implementation does nothing
	 * and should be overridden.
	 */
	public void close() {
		this.warn("close(): Not overridden");
	}
	
	/**
	 * Opens a simple confirmation window. 
	 */
	public boolean confirm(String message) {
		int retValue = JOptionPane.showConfirmDialog(htmlPanel, message, "Confirm", JOptionPane.YES_NO_OPTION);
		return retValue == JOptionPane.YES_OPTION;
	}
	
	/**
	 * It should request focus for the current browser window. This implementation does nothing
	 * and should be overridden.
	 */
	public void focus() {
		this.warn("focus(): Not overridden");
	}
	
	/**
	 * @deprecated Use {@link #open(URL, String, String, boolean)}.
	 */
	public final HtmlRendererContext open(String url, String windowName, String windowFeatures, boolean replace) {
		URL urlObj;
		try {
			urlObj = new URL(url);
		} catch(MalformedURLException mfu) {
			throw new IllegalArgumentException("Malformed URL: " + url);
		}
		return this.open(urlObj, windowName, windowFeatures, replace);
	}
	
	/**
	 * It should open a new browser window. This implementation does nothing
	 * and should be overridden.
	 * @param url The requested URL.
	 * @param windowName A window identifier.
	 * @param windowFeatures Window features specified in a format equivalent to
	 *        that of window.open() in Javascript.
	 * @param replace Whether an existing window with the same name should be replaced.
	 */
	public HtmlRendererContext open(java.net.URL url, String windowName, String windowFeatures, boolean replace) {
		this.warn("open(): Not overridden");
		return null;
	}
	
	/**
	 * Shows a simple prompt dialog.
	 */
	public String prompt(String message, String inputDefault) {
		return JOptionPane.showInputDialog(htmlPanel, message);
	}
	
	/**
	 * Changes the origin of the HTML block's scrollable area
	 * according to the position given.
	 * <p>
	 * This method may be called outside of the GUI thread.
	 * The operation is scheduled immediately in that thread as needed.
	 * @param x The new x coordinate for the origin.
	 * @param y The new y coordinate for the origin.
	 */
	public void scroll(int x, int y) {
		this.htmlPanel.scroll(x, y);
	}
	
	public void scrollBy(int x, int y) {
		this.htmlPanel.scrollBy(x, y);
	}
	
	/**
	 * Should return true if and only if the current browser window is closed. 
	 * This implementation returns false and should be overridden.
	 */
	public boolean isClosed() {
		this.warn("isClosed(): Not overridden");
		return false;
	}
	
	/**
	 * Should return true if and only if the current browser window is closed. 
	 * This implementation returns false and should be overridden.
	 */
	public String getDefaultStatus() {
		this.warn("getDefaultStatus(): Not overridden");
		return "";
	}
		
	/**
	 * It should return the name of the browser window, if this
	 * renderer context is for the top frame in the window. This
	 * implementation returns a blank string, so it should be overridden.
	 */
	public String getName() {
		this.warn("getName(): Not overridden");
		return "";
	}
	
	public HtmlRendererContext getParent() {
		return this.parentRcontext;
	}
	
	private volatile HtmlRendererContext opener;
	
	public HtmlRendererContext getOpener() {
		return this.opener;
	}
	
	public void setOpener(HtmlRendererContext opener) {
		this.opener = opener;
	}
	
	public String getStatus() {
		this.warn("getStatus(): Not overridden");
		return "";
	}
	
	public void setStatus(String message) {
		this.warn("setStatus(): Not overridden");
	}
	
	public HtmlRendererContext getTop() {
		HtmlRendererContext ancestor = this.parentRcontext;
		if(ancestor == null) {
			return this;
		}
		return ancestor.getTop();
	}
	
	public BrowserFrame createBrowserFrame() {
		return new SimpleBrowserFrame(this);
	}		
	
	public void warn(String message, Throwable throwable) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.log(Level.WARNING, message, throwable);
		}
	}
	
	public void error(String message, Throwable throwable) {
		if(logger.isLoggable(Level.SEVERE)) {
			logger.log(Level.SEVERE, message, throwable);
		}
	}
	
	public void warn(String message) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.log(Level.WARNING, message);
		}		
	}
	
	public void error(String message) {
		if(logger.isLoggable(Level.SEVERE)) {
			logger.log(Level.SEVERE, message);
		}
	}
	
	/**
	 * Returns <code>null</code>. This method should be overridden
	 * to provide OBJECT, EMBED or APPLET functionality. 
	 */
	public HtmlObject getHtmlObject(HTMLElement element) {
		return null;
	}
	
	public void setDefaultStatus(String message) {
		this.warn("setDefaultStatus(): Not overridden.");
	}
	
	private UserAgentContext bcontext = null;
	
	/**
	 * If a {@link org.lobobrowser.html.UserAgentContext} instance
	 * was provided in the constructor, then that instance is returned.
	 * Otherwise, an instance of {@link SimpleUserAgentContext} is 
	 * created and returned.
	 * <p>
	 * The context returned by this method is used by local request 
	 * facilities and other parts of the renderer.
	 */
	public UserAgentContext getUserAgentContext() {
		synchronized(this) {
			if(this.bcontext == null) {
				this.warn("getUserAgentContext(): UserAgentContext not provided in constructor. Creating a simple one.");
				this.bcontext = new AWLUserAgentContext();
			}
			return this.bcontext;
		}
	}
	
	/**
	 * Should be overridden to return true if the link
	 * has been visited.
	 */
	public boolean isVisitedLink(HTMLLinkElement link) {
		return false;
	}
	
	/**
	 * This method must be overridden to implement a context menu.
	 */
	public boolean onContextMenu(HTMLElement element, MouseEvent event) {
		return true;
	}
	
	/**
	 * This method can be overridden to receive notifications when the
	 * mouse leaves an element.
	 */
	public void onMouseOut(HTMLElement element, MouseEvent event) {
	}
	
	/**
	 * This method can be overridden to receive notifications when the
	 * mouse first enters an element.
	 */
	public void onMouseOver(HTMLElement element, MouseEvent event) {
	}
	
	public boolean isImageLoadingEnabled() {
		return true;
	}
	
	public boolean onDoubleClick(HTMLElement element, MouseEvent event) {
		return true;
	}
	
	public boolean onMouseClick(HTMLElement element, MouseEvent event) {
		return true;
	}
	
	private static java.awt.Window getWindow(Component c) {
		java.awt.Component current = c;
		while(current != null && !(current instanceof java.awt.Window)) {
			current = current.getParent();
		}
		return (java.awt.Window) current;
	}
	
	public void resizeBy(int byWidth, int byHeight) {
		java.awt.Window window = getWindow(this.htmlPanel);
		if(window != null) {
			window.setSize(window.getWidth() + byWidth, window.getHeight() + byHeight);
		}
	}
	
	public void resizeTo(int width, int height) {
		java.awt.Window window = getWindow(this.htmlPanel);
		if(window != null) {
			window.setSize(width, height);
		}
	}
	
	/**
	 * It should navigate back one page. This implementation does nothing
	 * and should be overridden.
	 */
	public void back() {
	    if(logger.isLoggable(Level.WARNING)) {
	        logger.log(Level.WARNING, "back() does nothing, unless overridden.");
	    }       
	}
	
	public void forward() {
	    if(logger.isLoggable(Level.WARNING)) {
	        logger.log(Level.WARNING, "forward() does nothing, unless overridden.");
	    }       
	}
	
	public String getCurrentURL() {
	    Object node = this.htmlPanel.getRootNode();
	    if(node instanceof HTMLDocumentImpl) {
	        HTMLDocumentImpl doc = (HTMLDocumentImpl) node;
	        return doc.getDocumentURI();
	    }
	    return null;
	}
	
	public int getHistoryLength() {
	    return 0;
	}
	
	public String getNextURL() {
	    return null;
	}
	
	public String getPreviousURL() {
	    return null;
	}
	
	public void goToHistoryURL(String url) {
	    if(logger.isLoggable(Level.WARNING)) {
	        logger.log(Level.WARNING, "goToHistoryURL() does nothing, unless overridden.");
	    }       
	}
	
	public void moveInHistory(int offset) {
	    if(logger.isLoggable(Level.WARNING)) {
	        logger.log(Level.WARNING, "moveInHistory() does nothing, unless overridden.");
	    }       
	}
}
