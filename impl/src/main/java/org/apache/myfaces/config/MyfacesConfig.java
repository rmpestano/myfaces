/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.StateManager;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFWebConfigParam;
import org.apache.myfaces.util.ClassUtils;

/**
 * Holds all configuration init parameters (from web.xml) that are independent
 * from the core implementation. The parameters in this class are available to
 * all shared, component and implementation classes.
 * See RuntimeConfig for configuration infos that come from the faces-config
 * files and are needed by the core implementation.
 *
 * MyfacesConfig is meant for components that implement some of the extended features
 * of MyFaces. Anyhow, using the MyFaces JSF implementation is no precondition for using
 * MyfacesConfig in custom components. Upon using another JSF implementation
 * (or omitting the extended init parameters) all config properties will simply have
 * their default values.
 */
public class MyfacesConfig
{
    private static final String APPLICATION_MAP_PARAM_NAME = MyfacesConfig.class.getName();

    /**
     * Set the time in seconds that check for updates of web.xml and faces-config descriptors and 
     * refresh the configuration.
     * This param is valid only if project stage is not production. Set this param to 0 disable this feature.
     */
    @JSFWebConfigParam(defaultValue="2",since="1.1", classType="java.lang.Long")
    public static final String CONFIG_REFRESH_PERIOD = "org.apache.myfaces.CONFIG_REFRESH_PERIOD";
    public static final long CONFIG_REFRESH_PERIOD_DEFAULT = 2;

    /**
     * Define if the input field that should store the state (javax.faces.ViewState) should render 
     * id="javax.faces.ViewState".
     * 
     * JSF API 1.2 defines a "javax.faces.ViewState" client parameter, that must be rendered as both the "name"
     * and the "id" attribute of the hidden input that is rendered for the purpose of state saving
     * (see ResponseStateManager.VIEW_STATE_PARAM).
     * Actually this causes duplicate id attributes and thus invalid XHTML pages when multiple forms are rendered on
     * one page. With the org.apache.myfaces.RENDER_VIEWSTATE_ID context parameter you can tune this behaviour.
     * <br/>Set it to
     * <ul><li>true - to render JSF 1.2 compliant id attributes (that might cause invalid XHTML), or</li>
     * <li>false - to omit rendering of the id attribute (which is only needed for very special 
     * AJAX/Javascript components)</li></ul>
     * Default value is: true (for backwards compatibility and JSF 1.2 compliancy) 
     */
    @JSFWebConfigParam(defaultValue="true", expectedValues="true, false, on, off, yes, no",since="1.1", 
            ignoreUpperLowerCase=true, group="state")
    private static final String  RENDER_VIEWSTATE_ID = "org.apache.myfaces.RENDER_VIEWSTATE_ID";
    private static final boolean RENDER_VIEWSTATE_ID_DEFAULT = true;

    /**
     * Use "&amp;amp;" entity instead a plain "&amp;" character within HTML.
     * <p>W3C recommends to use the "&amp;amp;" entity instead of a plain "&amp;" character within HTML.
     * This also applies to attribute values and thus to the "href" attribute of &lt;a&gt; elements as well.
     * Even more, when XHTML is used as output the usage of plain "&amp;" characters is forbidden and would lead to
     * invalid XML code.
     * Therefore, since version 1.1.6 MyFaces renders the correct "&amp;amp;" entity for links.</p>
     * <p>The init parameter
     * org.apache.myfaces.STRICT_XHTML_LINKS makes it possible to restore the old behaviour and to make MyFaces
     * "bug compatible" to the Sun RI which renders plain "&amp;" chars in links as well.</p>
     * <p>
     * See: <a href="http://www.w3.org/TR/html401/charset.html#h-5.3.2">HTML 4.01 Specification</a>
     * See: <a href="http://issues.apache.org/jira/browse/MYFACES-1774">Jira: MYFACES-1774</a>
     * </p>
     */
    @JSFWebConfigParam(defaultValue="true", expectedValues="true, false, on, off, yes, no",since="1.1.6", 
            ignoreUpperLowerCase=true, group="render")
    private static final String  STRICT_XHTML_LINKS = "org.apache.myfaces.STRICT_XHTML_LINKS";
    private static final boolean STRICT_XHTML_LINKS_DEFAULT = true;
    
    /**
     * This param renders the clear javascript on button necessary only for
     * compatibility with hidden fields feature of myfaces. This is done 
     * because jsf ri does not render javascript on onclick method for button,
     * so myfaces should do this.
     */
    @JSFWebConfigParam(defaultValue="false", expectedValues="true, false, on, off, yes, no",since="1.2.3",
            ignoreUpperLowerCase=true, group="render")
    private static final String RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON = 
        "org.apache.myfaces.RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON";
    private static final boolean RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON_DEFAULT= false;

    /**
     * Define an alternate class name that will be used to initialize MyFaces, instead the default 
     * javax.faces.webapp.FacesServlet.
     * 
     * <p>This helps MyFaces to detect the mappings and other additional configuration used to setup the 
     * environment, and prevent abort initialization if no FacesServlet config is detected.
     * </p>
     */
    @JSFWebConfigParam(since="1.2.7")
    private static final String DELEGATE_FACES_SERVLET = "org.apache.myfaces.DELEGATE_FACES_SERVLET";

    /**
     * Indicate if the facelet associated to the view should be reapplied when the view is refreshed.
     *  Default mode is "auto".
     * 
     * <p>This param is only valid when partial state saving is on.
     * If this is set as true, the tag-handlers are always reapplied before render view, like in facelets 1.1.x, 
     * allowing c:if work correctly to "toggle" components based on a value changed on invoke application phase. 
     * If the param is set as "auto", the implementation check if c:if, c:forEach, 
     * c:choose and ui:include with src=ELExpression is used on the page and if that so, mark the view
     * to be refreshed.</p> 
     */
    @JSFWebConfigParam(since="2.0", defaultValue="auto", expectedValues="true,false,auto", tags="performance", 
            ignoreUpperLowerCase=true, group="state")
    public final static String REFRESH_TRANSIENT_BUILD_ON_PSS = 
        "org.apache.myfaces.REFRESH_TRANSIENT_BUILD_ON_PSS"; 
    public final static String REFRESH_TRANSIENT_BUILD_ON_PSS_DEFAULT = "auto";

    /**
     * Enable or disable a special mode that enable full state for parent components containing c:if, c:forEach, 
     * c:choose and ui:include with src=ELExpression. By default is disabled(false).
     * 
     * <p>This param is only valid when partial state saving is on.
     * If this is set as true, parent components containing  c:if, c:forEach, 
     * c:choose and ui:include with src=ELExpression are marked to be restored fully, so state
     * is preserved between request.</p>
     */
    @JSFWebConfigParam(since="2.0", defaultValue="false", expectedValues="true, false, on, off, yes, no", 
            tags="performance", ignoreUpperLowerCase=true, group="state")
    public final static String REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE = 
        "org.apache.myfaces.REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE";
    public final static boolean REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE_DEFAULT = false;
    
    /**
     * If set to <code>true</code>, tag library XML files and faces config XML files using schema 
     * will be validated during application start up
     */
    @JSFWebConfigParam(since="2.0", expectedValues="true, false, on, off, yes, no", ignoreUpperLowerCase=true)
    public final static String VALIDATE_XML = "org.apache.myfaces.VALIDATE_XML";
    public final static boolean VALIDATE_XML_DEFAULT = false;
    
    /**
     * Wrap content inside script with xml comment to prevent old browsers to display it. By default it is true. 
     */
    @JSFWebConfigParam(since="2.0.1", expectedValues="true, false, on, off, yes, no", defaultValue="false",
            ignoreUpperLowerCase=true, group="render")
    public final static String WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG = 
        "org.apache.myfaces.WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG";
    public final static boolean WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG_DEFAULT = false;
    
    /**
     * If set true, render the form submit script inline, as in myfaces core 1.2 and earlier versions 
     */
    @JSFWebConfigParam(since="2.0.2", expectedValues="true, false, on, off, yes, no", defaultValue="false", 
            ignoreUpperLowerCase=true, group="render")
    public final static String RENDER_FORM_SUBMIT_SCRIPT_INLINE = 
        "org.apache.myfaces.RENDER_FORM_SUBMIT_SCRIPT_INLINE";
    public final static boolean RENDER_FORM_SUBMIT_SCRIPT_INLINE_DEFAULT = false;
    
    /**
     * Enable/disable DebugPhaseListener feature, with provide useful information about ValueHolder 
     * variables (submittedValue, localValue, value).
     * Note evaluate those getters for each component could cause some unwanted side effects when 
     * using "access" type scopes like on MyFaces CODI.
     * This param only has effect when project stage is Development.     
     */
    @JSFWebConfigParam(since="2.0.8")
    public final static String DEBUG_PHASE_LISTENER = "org.apache.myfaces.DEBUG_PHASE_LISTENER";
    public final static boolean DEBUG_PHASE_LISTENER_DEFAULT = false;
    
    /**
     * Change default getType() behavior for composite component EL resolver, from return null 
     * (see JSF 2_0 spec section 5_6_2_2) to
     * use the metadata information added by composite:attribute, ensuring components working with 
     * chained EL expressions to find the
     * right type when a getType() is called over the source EL expression.
     * 
     * To ensure strict compatibility with the spec set this param to true (by default is false, 
     * so the change is enabled by default). 
     */
    @JSFWebConfigParam(since="2.0.10", expectedValues="true, false", defaultValue="false", group="EL")
    public final static String STRICT_JSF_2_CC_EL_RESOLVER = 
        "org.apache.myfaces.STRICT_JSF_2_CC_EL_RESOLVER";
    public final static boolean STRICT_JSF_2_CC_EL_RESOLVER_DEFAULT = false;
    
    /**
     * Define the default content type that the default ResponseWriter generates, when no match can be derived from
     * HTTP Accept Header.
     */
    @JSFWebConfigParam(since="2.0.11,2.1.5", expectedValues="text/html, application/xhtml+xml", 
            defaultValue="text/html", group="render")
    public final static String DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE = 
        "org.apache.myfaces.DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE";
    public final static String DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE_DEFAULT = "text/html";

    /**
     * Enable or disable a cache used to "remember" the generated facelets unique ids and reduce 
     * the impact on memory usage, only active if javax.faces.FACELETS_REFRESH_PERIOD is -1 (no refresh).
     */
    @JSFWebConfigParam(defaultValue = "true", since = "2.0.13, 2.1.7", expectedValues="true, false", 
            group="viewhandler", tags="performance",
            desc="Enable or disable a cache used to 'remember'  the generated facelets unique ids " + 
                 "and reduce the impact over memory usage.")
    public static final String VIEW_UNIQUE_IDS_CACHE_ENABLED = 
        "org.apache.myfaces.VIEW_UNIQUE_IDS_CACHE_ENABLED";
    public static final boolean VIEW_UNIQUE_IDS_CACHE_ENABLED_DEFAULT = true;
    
    /**
     * Set the size of the cache used to store strings generated using SectionUniqueIdCounter
     * for component ids. If this is set to 0, no cache is used. By default is set to 100.
     */
    @JSFWebConfigParam(defaultValue = "100", since = "2.0.13, 2.1.7",
            group="viewhandler", tags="performance")
    public static final String COMPONENT_UNIQUE_IDS_CACHE_SIZE =
        "org.apache.myfaces.COMPONENT_UNIQUE_IDS_CACHE_SIZE";
    public static final int COMPONENT_UNIQUE_IDS_CACHE_SIZE_DEFAULT = 100;

    /**
    * If set false, myfaces won't support JSP and javax.faces.el. JSP are deprecated in JSF 2.X, javax.faces.el in 
    * in JSF 1.2. Default value is true. 
    * 
    * If this property is set is false, JSF 1.1 VariableResolver and PropertyResolver config (replaced in JSF 1.2 by
    * ELResolver) and all related logic for JSP is skipped, making EL evaluation faster.  
    */
    @JSFWebConfigParam(since="2.0.13,2.1.7", expectedValues="true,false", defaultValue="true",
         desc="If set false, myfaces won't support JSP and javax.faces.el. JSP are deprecated in " +
         "JSF 2.X, javax.faces.el in in JSF 1.2. Default value is true.",
         group="EL", tags="performance ")
    public final static String SUPPORT_JSP_AND_FACES_EL = "org.apache.myfaces.SUPPORT_JSP_AND_FACES_EL";
    public final static boolean SUPPORT_JSP_AND_FACES_EL_DEFAULT = true;
    
    /**
     * When the application runs inside Google Application Engine container (GAE),
     * indicate which jar files should be scanned for files (faces-config, facelets taglib
     * or annotations). It accept simple wildcard patterns like myfavoritejsflib-*.jar or 
     * myfavoritejsflib-1.1.?.jar. By default, all the classpath is scanned for files 
     * annotations (so it adds an small delay on startup).
     */
    @JSFWebConfigParam(since = "2.1.8, 2.0.14", expectedValues="none, myfavoritejsflib-*.jar",
            tags="performance, GAE")
    public static final String GAE_JSF_JAR_FILES = "org.apache.myfaces.GAE_JSF_JAR_FILES";
    public final static String GAE_JSF_JAR_FILES_DEFAULT = null;

    /**
     * When the application runs inside Google Application Engine container (GAE),
     * indicate which jar files should be scanned for annotations. This param overrides
     * org.apache.myfaces.GAE_JSF_JAR_FILES behavior that tries to find faces-config.xml or
     * files ending with .faces-config.xml in /META-INF folder and if that so, try to
     * find JSF annotations in the whole jar file. It accept simple wildcard patterns 
     * like myfavoritejsflib-*.jar or myfavoritejsflib-1.1.?.jar.
     * By default, all the classpath is scanned for annotations (so it adds an small
     * delay on startup).
     */
    @JSFWebConfigParam(since = "2.1.8, 2.0.14", expectedValues="none, myfavoritejsflib-*.jar",
            tags="performance, GAE")
    public static final String GAE_JSF_ANNOTATIONS_JAR_FILES = 
            "org.apache.myfaces.GAE_JSF_ANNOTATIONS_JAR_FILES";
    public final static String GAE_JSF_ANNOTATIONS_JAR_FILES_DEFAULT = null;
    
    /**
     * If this param is set to true, a check will be done in Restore View Phase to check
     * if the viewId exists or not and if it does not exists, a 404 response will be thrown.
     * 
     * This is applicable in cases where all the views in the application are generated by a 
     * ViewDeclarationLanguage implementation.
     */
    @JSFWebConfigParam(since = "2.1.13", defaultValue="false", expectedValues="true,false", 
            group="viewhandler")
    public static final String STRICT_JSF_2_VIEW_NOT_FOUND = 
            "org.apache.myfaces.STRICT_JSF_2_VIEW_NOT_FOUND";
    public final static boolean STRICT_JSF_2_VIEW_NOT_FOUND_DEFAULT = false;

    @JSFWebConfigParam(defaultValue = "false", since = "2.2.0", expectedValues="true, false", group="render",
            tags="performance",
            desc="Enable or disable an early flush which allows to send e.g. the HTML-Head to the client " +
                    "while the rest gets rendered. It's a well known technique to reduce the time for loading a page.")
    private static final String EARLY_FLUSH_ENABLED =
        "org.apache.myfaces.EARLY_FLUSH_ENABLED";
    private static final boolean EARLY_FLUSH_ENABLED_DEFAULT = false;
    
    /**
     * This param makes components like c:set, ui:param and templating components like ui:decorate,
     * ui:composition and ui:include to behave like the ones provided originally in facelets 1_1_x. 
     * See MYFACES-3810 for details.
     */
    @JSFWebConfigParam(since = "2.2.0", defaultValue="false", expectedValues="true,false", 
            group="viewhandler")
    public static final String STRICT_JSF_2_FACELETS_COMPATIBILITY = 
            "org.apache.myfaces.STRICT_JSF_2_FACELETS_COMPATIBILITY";
    public final static boolean STRICT_JSF_2_FACELETS_COMPATIBILITY_DEFAULT = false;    
    
    /**
     * This param makes h:form component to render the view state and other hidden fields
     * at the beginning of the form. This also includes component resources with target="form",
     * but it does not include legacy 1.1 myfaces specific hidden field adition.
     */
    @JSFWebConfigParam(since = "2.2.4", defaultValue = "false", expectedValues = "true,false",
            group="render")
    public static final String RENDER_FORM_VIEW_STATE_AT_BEGIN =
            "org.apache.myfaces.RENDER_FORM_VIEW_STATE_AT_BEGIN";
    public final static boolean RENDER_FORM_VIEW_STATE_AT_BEGIN_DEFAULT = false;
    
    /**
     * Defines whether flash scope is disabled, preventing add the Flash cookie to the response. 
     * 
     * <p>This is useful for applications that does not require to use flash scope, and instead uses other scopes.</p>
     */
    @JSFWebConfigParam(defaultValue="false",since="2.0.5")
    public static final String FLASH_SCOPE_DISABLED = "org.apache.myfaces.FLASH_SCOPE_DISABLED";
    public static final boolean FLASH_SCOPE_DISABLED_DEFAULT = false;
    
    /**
     * Defines the amount (default = 20) of the latest views are stored in session.
     * 
     * <p>Only applicable if state saving method is "server" (= default).
     * </p>
     * 
     */
    @JSFWebConfigParam(defaultValue="20",since="1.1", classType="java.lang.Integer", group="state", tags="performance")
    public static final String NUMBER_OF_VIEWS_IN_SESSION = "org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION";
    /**
     * Default value for <code>org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION</code> context parameter.
     */
    public static final int NUMBER_OF_VIEWS_IN_SESSION_DEFAULT = 20;    

    /**
     * Indicates the amount of views (default is not active) that should be stored in session between sequential
     * POST or POST-REDIRECT-GET if org.apache.myfaces.USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION is true.
     * 
     * <p>Only applicable if state saving method is "server" (= default). For example, if this param has value = 2 and 
     * in your custom webapp there is a form that is clicked 3 times, only 2 views
     * will be stored and the third one (the one stored the first time) will be
     * removed from session, even if the view can
     * store more sessions org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION.
     * This feature becomes useful for multi-window applications.
     * where without this feature a window can swallow all view slots so
     * the other ones will throw ViewExpiredException.</p>
     */
    @JSFWebConfigParam(since="2.0.6", classType="java.lang.Integer", group="state", tags="performance", 
            defaultValue = "4")
    public static final String NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION
            = "org.apache.myfaces.NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION";
    public static final Integer NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT = 4;
    
    /**
     * Indicate the max number of flash tokens stored into session. It is only active when 
     * javax.faces.CLIENT_WINDOW_MODE is enabled and javax.faces.STATE_SAVING_METHOD is set
     * to "server". Each flash token is associated to one client window id at
     * the same time, so this param is related to the limit of active client windows per session. 
     * By default is the same number as in 
     * (org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION / 
     * org.apache.myfaces.NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION) + 1 = 6.
     */
    @JSFWebConfigParam(since="2.2.6", group="state", tags="performance")
    static final String NUMBER_OF_FLASH_TOKENS_IN_SESSION = 
            "org.apache.myfaces.NUMBER_OF_FLASH_TOKENS_IN_SESSION";
    
    /**
     * Indicate the max number of client window ids stored into session by faces flow. It is only active when 
     * javax.faces.CLIENT_WINDOW_MODE is enabled and javax.faces.STATE_SAVING_METHOD is set
     * to "server". This param is related to the limit of active client 
     * windows per session, and it is used to cleanup flow scope beans when a client window or view becomes 
     * invalid. 
     * By default is the same number as in 
     * (org.apache.myfaces.NUMBER_OF_VIEWS_IN_SESSION / 
     * org.apache.myfaces.NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION) + 1 = 6.
     */
    @JSFWebConfigParam(since="2.2.6", group="state", tags="performance")
    static final String NUMBER_OF_FACES_FLOW_CLIENT_WINDOW_IDS_IN_SESSION = 
            "org.apache.myfaces.FACES_FLOW_CLIENT_WINDOW_IDS_IN_SESSION";
    
    /**
     * This parameter specifies whether or not the ImportHandler will be supported
     */
    @JSFWebConfigParam(since="2.2.9", defaultValue="false", expectedValues="true,false", group="EL")
    protected static final String SUPPORT_EL_3_IMPORT_HANDLER = "org.apache.myfaces.SUPPORT_EL_3_IMPORT_HANDLER";
    public final static boolean SUPPORT_EL_3_IMPORT_HANDLER_DEFAULT = false;

    /**
     * This parameter specifies whether or not the Origin header app path should be checked 
     */
    @JSFWebConfigParam(since="2.3", defaultValue="false", expectedValues="true,false")
    protected static final String STRICT_JSF_2_ORIGIN_HEADER_APP_PATH = 
            "org.apache.myfaces.STRICT_JSF_2_ORIGIN_HEADER_APP_PATH";
    public final static boolean STRICT_JSF_2_ORIGIN_HEADER_APP_PATH_DEFAULT = false;

    /**
     * Allow slash in the library name of a Resource. 
     */
    @JSFWebConfigParam(since="2.1.6, 2.0.12", defaultValue="false", 
            expectedValues="true, false", group="resources")
    public static final String STRICT_JSF_2_ALLOW_SLASH_LIBRARY_NAME = 
            "org.apache.myfaces.STRICT_JSF_2_ALLOW_SLASH_LIBRARY_NAME";
    public static final boolean STRICT_JSF_2_ALLOW_SLASH_LIBRARY_NAME_DEFAULT = false;
    
    /**
     * Define the default buffer size that is used between Resource.getInputStream() and 
     * httpServletResponse.getOutputStream() when rendering resources using the default
     * ResourceHandler.
     */
    @JSFWebConfigParam(since="2.1.10, 2.0.16", defaultValue="2048", group="resources")
    public static final String RESOURCE_BUFFER_SIZE = "org.apache.myfaces.RESOURCE_BUFFER_SIZE";
    public static final int RESOURCE_BUFFER_SIZE_DEFAULT = 2048;
    
    /**
     * Validate if the managed beans and navigations rules are correct.
     * 
     * <p>For example, it checks if the managed bean classes really exists, or if the 
     * navigation rules points to existing view files.</p>
     */
    @JSFWebConfigParam(since="2.0", defaultValue="false", expectedValues="true, false")
    public static final String VALIDATE = "org.apache.myfaces.VALIDATE";
    
    /**
     * Defines if CDI should be used for annotation scanning to improve the startup performance.
     */
    @JSFWebConfigParam(since="2.2.9", tags = "performance", defaultValue = "false")
    public static final String USE_CDI_FOR_ANNOTATION_SCANNING
            = "org.apache.myfaces.annotation.USE_CDI_FOR_ANNOTATION_SCANNING";
    public static final boolean USE_CDI_FOR_ANNOTATION_SCANNING_DEFAULT = false;
    
    
    /**
     * Controls the size of the cache used to check if a resource exists or not. 
     * 
     * <p>See org.apache.myfaces.RESOURCE_HANDLER_CACHE_ENABLED for details.</p>
     */
    @JSFWebConfigParam(defaultValue = "500", since = "2.0.2", group="resources", 
            classType="java.lang.Integer", tags="performance")
    private static final String RESOURCE_HANDLER_CACHE_SIZE = 
        "org.apache.myfaces.RESOURCE_HANDLER_CACHE_SIZE";
    private static final int RESOURCE_HANDLER_CACHE_SIZE_DEFAULT = 500;

    /**
     * Enable or disable the cache used to "remember" if a resource handled by 
     * the default ResourceHandler exists or not.
     * 
     */
    @JSFWebConfigParam(defaultValue = "true", since = "2.0.2", group="resources", 
            expectedValues="true,false", tags="performance")
    private static final String RESOURCE_HANDLER_CACHE_ENABLED = 
        "org.apache.myfaces.RESOURCE_HANDLER_CACHE_ENABLED";
    private static final boolean RESOURCE_HANDLER_CACHE_ENABLED_DEFAULT = true;
    
    /**
     * Servlet context init parameter which defines which packages to scan
     * for beans, separated by commas.
     */
    @JSFWebConfigParam(since="2.0")
    public static final String SCAN_PACKAGES = "org.apache.myfaces.annotation.SCAN_PACKAGES";
 
    /**
     * Indicates the port used for websocket connections.
     */
    @JSFWebConfigParam(since = "2.3")
    public static final java.lang.String WEBSOCKET_ENDPOINT_PORT = "javax.faces.WEBSOCKET_ENDPOINT_PORT";

    @JSFWebConfigParam(defaultValue = "300000")
    public static final String WEBSOCKET_MAX_IDLE_TIMEOUT = "org.apache.myfaces.WEBSOCKET_MAX_IDLE_TIMEOUT";
    public static final long WEBSOCKET_MAX_IDLE_TIMEOUT_DEFAULT = 300000L;
    
    /**
     * Defines how to generate the csrf session token.
     */
    @JSFWebConfigParam(since="2.2.0", expectedValues="secureRandom, random", 
            defaultValue="none", group="state")
    public static final String RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN
            = "org.apache.myfaces.RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN";
    public static final String RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN_DEFAULT = "random";
  
    public static final String RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN_SECURE_RANDOM = "secureRandom";
    public static final String RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN_RANDOM = "random";
    
    /**
     * Define the time in minutes where the view state is valid when
     * client side state saving is used. By default it is set to 0
     * (infinite).
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", defaultValue="0", group="state")
    public static final String CLIENT_VIEW_STATE_TIMEOUT = 
            "org.apache.myfaces.CLIENT_VIEW_STATE_TIMEOUT";
    public static final Long CLIENT_VIEW_STATE_TIMEOUT_DEFAULT = 0L;
   
    
    /**
     * Adds a random key to the generated view state session token.
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", expectedValues="secureRandom, random", 
            defaultValue="random", group="state")
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN
            = "org.apache.myfaces.RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN";
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_DEFAULT = "random";
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM = "secureRandom";
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_RANDOM = "random";
    
    /**
     * Set the default length of the random key added to the view state session token.
     * By default is 8. 
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", defaultValue="8", group="state")
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH
            = "org.apache.myfaces.RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH";
    public static final int RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH_DEFAULT = 8;
    
    /**
     * Sets the random class to initialize the secure random id generator. 
     * By default it uses java.security.SecureRandom
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", group="state")
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_CLASS
            = "org.apache.myfaces.RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_CLASS";
    
    /**
     * Sets the random provider to initialize the secure random id generator.
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", group="state")
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_PROVIDER
            = "org.apache.myfaces.RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_PROVIDER";
    
    /**
     * Sets the random algorithm to initialize the secure random id generator. 
     * By default is SHA1PRNG
     */
    @JSFWebConfigParam(since="2.1.9, 2.0.15", defaultValue="SHA1PRNG", group="state")
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM
            = "org.apache.myfaces.RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM";
    public static final String RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM_DEFAULT = "SHA1PRNG";
    
    /**
     * Defines how to generate the csrf session token.
     */
    @JSFWebConfigParam(since="2.2.0", expectedValues="secureRandom, random", defaultValue="none", group="state")
    public static final String RANDOM_KEY_IN_CSRF_SESSION_TOKEN
            = "org.apache.myfaces.RANDOM_KEY_IN_CSRF_SESSION_TOKEN";
    public static final String RANDOM_KEY_IN_CSRF_SESSION_TOKEN_DEFAULT = "random";
    
    public static final String RANDOM_KEY_IN_CSRF_SESSION_TOKEN_SECURE_RANDOM = "secureRandom";
    public static final String RANDOM_KEY_IN_CSRF_SESSION_TOKEN_RANDOM = "random";
    
    /**
     * Indicates that the serialized state will be compressed before it is written to the session. By default true.
     * 
     * Only applicable if state saving method is "server" (= default) and if
     * <code>org.apache.myfaces.SERIALIZE_STATE_IN_SESSION</code> is <code>true</code> (= default).
     * If <code>true</code> (default) the serialized state will be compressed before it is written to the session.
     * If <code>false</code> the state will not be compressed.
     */
    @JSFWebConfigParam(defaultValue="true",since="1.1", expectedValues="true,false", group="state", tags="performance")
    public static final String COMPRESS_STATE_IN_SESSION = "org.apache.myfaces.COMPRESS_STATE_IN_SESSION";
    public static final boolean COMPRESS_STATE_IN_SESSION_DEFAULT = true;
    
    /**
     * Allow use flash scope to keep track of the views used in session and the previous ones,
     * so server side state saving can delete old views even if POST-REDIRECT-GET pattern is used.
     * 
     * <p>
     * Only applicable if state saving method is "server" (= default).
     * The default value is false.</p>
     */
    @JSFWebConfigParam(since="2.0.6", defaultValue="false", expectedValues="true, false", group="state")
    public static final String USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION
            = "org.apache.myfaces.USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION";
    public static final boolean USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION_DEFAULT = false;
    
    /**
     * Add autocomplete="off" to the view state hidden field. Enabled by default.
     */
    @JSFWebConfigParam(since="2.2.8, 2.1.18, 2.0.24", expectedValues="true, false", 
           defaultValue="true", group="state")
    public static final String AUTOCOMPLETE_OFF_VIEW_STATE = 
            "org.apache.myfaces.AUTOCOMPLETE_OFF_VIEW_STATE";
    public static final boolean AUTOCOMPLETE_OFF_VIEW_STATE_DEFAULT = true;
    
    /**
     * Set the max time in miliseconds set on the "Expires" header for a resource rendered by 
     * the default ResourceHandler.
     * (default to one week in miliseconds or 604800000) 
     */
    @JSFWebConfigParam(since="2.0", defaultValue="604800000", group="resources", tags="performance")
    public static final String RESOURCE_MAX_TIME_EXPIRES = "org.apache.myfaces.RESOURCE_MAX_TIME_EXPIRES";
    public static final long RESOURCE_MAX_TIME_EXPIRES_DEFAULT = 604800000L;
    

    /**
     * Indicate if the classes associated to components, converters, validators or behaviors
     * should be loaded as soon as they are added to the current application instance or instead
     * loaded in a lazy way.
     */
    @JSFWebConfigParam(defaultValue="true",since="2.0",tags="performance")
    private static final String LAZY_LOAD_CONFIG_OBJECTS = "org.apache.myfaces.LAZY_LOAD_CONFIG_OBJECTS";
    private static final boolean LAZY_LOAD_CONFIG_OBJECTS_DEFAULT = true;

    
    /**
     * Define a custom comparator class used to sort the ELResolvers.
     * 
     * <p>This is useful when it is necessary to put an ELResolver on top of other resolvers. Note set
     * this param override the default ordering described by JSF spec section 5. 
     * </p>
     */
    @JSFWebConfigParam(since = "1.2.10, 2.0.2", group="EL",
            desc = "The Class of an Comparator&lt;ELResolver&gt; implementation.")
    public static final String EL_RESOLVER_COMPARATOR = "org.apache.myfaces.EL_RESOLVER_COMPARATOR";
    
    @JSFWebConfigParam(since = "2.1.0", group="EL",
        desc="The Class of an java.util.function.Predicate&lt;ELResolver&gt; implementation."
             + "If used and returns false for a ELResolver instance, such resolver will not be installed in "
             + "ELResolvers chain. Use with caution - can break functionality defined in JSF specification "
             + "'ELResolver Instances Provided by Faces'")
    public static final String EL_RESOLVER_PREDICATE = "org.apache.myfaces.EL_RESOLVER_PREDICATE";
    
    /**
     * Controls the size of the cache used to "remember" if a view exists or not.
     */
    @JSFWebConfigParam(defaultValue = "500", since = "2.0.2", group="viewhandler", tags="performance", 
            classType="java.lang.Integer",
            desc="Controls the size of the cache used to 'remember' if a view exists or not.")
    private static final String CHECKED_VIEWID_CACHE_SIZE = "org.apache.myfaces.CHECKED_VIEWID_CACHE_SIZE";
    private static final int CHECKED_VIEWID_CACHE_SIZE_DEFAULT = 500;

    /**
     * Enable or disable a cache used to "remember" if a view exists or not and reduce the impact of
     * sucesive calls to ExternalContext.getResource().
     */
    @JSFWebConfigParam(defaultValue = "true", since = "2.0.2", expectedValues="true, false", group="viewhandler", 
            tags="performance",
            desc="Enable or disable a cache used to 'remember' if a view exists or not and reduce the impact " +
                 "of sucesive calls to ExternalContext.getResource().")
    private static final String CHECKED_VIEWID_CACHE_ENABLED = "org.apache.myfaces.CHECKED_VIEWID_CACHE_ENABLED";
    private static final boolean CHECKED_VIEWID_CACHE_ENABLED_DEFAULT = true;
    
    @JSFWebConfigParam(defaultValue=".jsp", since="2.3", group="viewhandler")
    public static final String JSP_SUFFIX = "org.apache.myfaces.JSP_SUFFIX";
    public static final String JSP_SUFFIX_DEFAULT = ".jsp";

    /**
     * Enforce f:validateBean to be called first before any JSF validator.
     */
    @JSFWebConfigParam(defaultValue="false", expectedValues="true, false", since = "2.2.10", group="validation")
    private final static String BEAN_BEFORE_JSF_VALIDATION
            = "org.apache.myfaces.validator.BEAN_BEFORE_JSF_VALIDATION";
    private final static boolean BEAN_BEFORE_JSF_VALIDATION_DEFAULT = false;

    /**
     * comma delimited list of plugin classes which can be hooked into myfaces
     */
    @JSFWebConfigParam(since = "2.0")
    public static final String FACES_INIT_PLUGINS = "org.apache.myfaces.FACES_INIT_PLUGINS";
    
    /**
     * If the flag is true, the algoritm skip jar scanning for faces-config files to check if the current
     * application requires FacesServlet to be added dynamically (servlet spec 3). This param can be set using 
     * a system property with the same name too.
     */
    @JSFWebConfigParam(since="2.2.10", expectedValues = "true, false", defaultValue = "false", 
            tags = "performance")
    public static final String INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN = 
            "org.apache.myfaces.INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN";
    public static final boolean INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN_DEFAULT = false;
    
    /**
     * If this param is set to true, the check for faces servlet mapping is not done 
     */
    @JSFWebConfigParam(since="2.0.3", defaultValue="false")
    public static final String INITIALIZE_ALWAYS_STANDALONE = "org.apache.myfaces.INITIALIZE_ALWAYS_STANDALONE";

    /**
     * This parameter specifies the ExpressionFactory implementation to use.
     */
    @JSFWebConfigParam(since="1.2.7", group="EL")
    public static final String EXPRESSION_FACTORY = "org.apache.myfaces.EXPRESSION_FACTORY";
   
    /**
     * Define how duplicate ids are checked when ProjectStage is Production, by default (auto) it only check ids of
     * components that does not encapsulate markup (like facelets UILeaf).
     *  
     * <ul>
     * <li>true: check all ids, including ids for components that are transient and encapsulate markup.</li>
     * <li>auto: (default) check ids of components that does not encapsulate markup (like facelets UILeaf). 
     * Note ids of UILeaf instances are generated by facelets vdl, start with "j_id", are never rendered 
     * into the response and UILeaf instances are never used as a target for listeners, so in practice 
     * there is no need to check such ids. This reduce the overhead associated with generate client ids.</li>
     * <li>false: do not do any check when ProjectStage is Production</li>
     * </ul>
     * <p> According to specification, identifiers must be unique within the scope of the nearest ancestor to 
     * the component that is a naming container.</p>
     */
    @JSFWebConfigParam(since="2.0.12, 2.1.6", defaultValue="auto", expectedValues="true, auto, false",
                       group="state", tags="performance")
    public static final String CHECK_ID_PRODUCTION_MODE
            = "org.apache.myfaces.CHECK_ID_PRODUCTION_MODE";
    public static final String CHECK_ID_PRODUCTION_MODE_DEFAULT = "auto";
    public static final String CHECK_ID_PRODUCTION_MODE_TRUE = "true";
    public static final String CHECK_ID_PRODUCTION_MODE_AUTO = "auto";
    
    private boolean strictJsf2AllowSlashLibraryName;
    private long configRefreshPeriod = CONFIG_REFRESH_PERIOD_DEFAULT;
    private boolean renderViewStateId = RENDER_VIEWSTATE_ID_DEFAULT;
    private boolean strictXhtmlLinks = STRICT_XHTML_LINKS_DEFAULT;
    private boolean renderClearJavascriptOnButton = RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON_DEFAULT;
    private String delegateFacesServlet;
    private boolean refreshTransientBuildOnPSS = true;
    private boolean refreshTransientBuildOnPSSAuto = true;
    private boolean refreshTransientBuildOnPSSPreserveState = REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE_DEFAULT;
    private boolean validateXML = VALIDATE_XML_DEFAULT;
    private boolean wrapScriptContentWithXmlCommentTag = WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG_DEFAULT;
    private boolean renderFormSubmitScriptInline = RENDER_FORM_SUBMIT_SCRIPT_INLINE_DEFAULT;
    private boolean debugPhaseListenerEnabled = DEBUG_PHASE_LISTENER_DEFAULT;
    private boolean strictJsf2CCELResolver = STRICT_JSF_2_CC_EL_RESOLVER_DEFAULT;
    private String defaultResponseWriterContentTypeMode = DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE_DEFAULT;
    private boolean viewUniqueIdsCacheEnabled = VIEW_UNIQUE_IDS_CACHE_ENABLED_DEFAULT;
    private int componentUniqueIdsCacheSize = COMPONENT_UNIQUE_IDS_CACHE_SIZE_DEFAULT;
    private boolean supportJSPAndFacesEL = SUPPORT_JSP_AND_FACES_EL_DEFAULT;
    private String gaeJsfJarFiles = GAE_JSF_JAR_FILES_DEFAULT;
    private String gaeJsfAnnotationsJarFiles = GAE_JSF_ANNOTATIONS_JAR_FILES_DEFAULT;
    private boolean strictJsf2ViewNotFound = STRICT_JSF_2_VIEW_NOT_FOUND_DEFAULT;
    private boolean earlyFlushEnabled = EARLY_FLUSH_ENABLED_DEFAULT;
    private boolean strictJsf2FaceletsCompatibility = STRICT_JSF_2_FACELETS_COMPATIBILITY_DEFAULT;
    private boolean renderFormViewStateAtBegin = RENDER_FORM_VIEW_STATE_AT_BEGIN_DEFAULT;
    private boolean flashScopeDisabled = FLASH_SCOPE_DISABLED_DEFAULT;
    private Integer numberOfViewsInSession = NUMBER_OF_VIEWS_IN_SESSION_DEFAULT;
    private Integer numberOfSequentialViewsInSession = NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT;
    private Integer numberOfFlashTokensInSession;
    private Integer numberOfFacesFlowClientWindowIdsInSession;
    private boolean supportEL3ImportHandler = SUPPORT_EL_3_IMPORT_HANDLER_DEFAULT;
    private boolean strictJsf2OriginHeaderAppPath = STRICT_JSF_2_ORIGIN_HEADER_APP_PATH_DEFAULT;
    private int resourceBufferSize = RESOURCE_BUFFER_SIZE_DEFAULT;
    private boolean useCdiForAnnotationScanning = USE_CDI_FOR_ANNOTATION_SCANNING_DEFAULT;
    private boolean resourceHandlerCacheEnabled = RESOURCE_HANDLER_CACHE_ENABLED_DEFAULT;
    private int resourceHandlerCacheSize = RESOURCE_HANDLER_CACHE_SIZE_DEFAULT;
    private String scanPackages;
    private long websocketMaxIdleTimeout = WEBSOCKET_MAX_IDLE_TIMEOUT_DEFAULT;
    private Integer websocketEndpointPort;
    private String randomKeyInWebsocketSessionToken = RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN_DEFAULT;
    private long clientViewStateTimeout = CLIENT_VIEW_STATE_TIMEOUT_DEFAULT;
    private String randomKeyInViewStateSessionToken = RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_DEFAULT;
    private int randomKeyInViewStateSessionTokenLength = RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH_DEFAULT;
    private String randomKeyInViewStateSessionTokenSecureRandomClass;
    private String randomKeyInViewStateSessionTokenSecureRandomProvider;
    private String randomKeyInViewStateSessionTokenSecureRandomAlgorithm
            = RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM_DEFAULT;
    private String randomKeyInCsrfSessionToken = RANDOM_KEY_IN_CSRF_SESSION_TOKEN_DEFAULT;
    private boolean serializeStateInSession = false;
    private boolean compressStateInSession = COMPRESS_STATE_IN_SESSION_DEFAULT;
    private boolean useFlashScopePurgeViewsInSession = USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION_DEFAULT;
    private boolean autocompleteOffViewState = AUTOCOMPLETE_OFF_VIEW_STATE_DEFAULT;
    private long resourceMaxTimeExpires = RESOURCE_MAX_TIME_EXPIRES_DEFAULT;
    private boolean lazyLoadConfigObjects = LAZY_LOAD_CONFIG_OBJECTS_DEFAULT;
    private String elResolverComparator;
    private String elResolverPredicate;
    private boolean checkedViewIdCacheEnabled = CHECKED_VIEWID_CACHE_ENABLED_DEFAULT;
    private int checkedViewIdCacheSize = CHECKED_VIEWID_CACHE_SIZE_DEFAULT;
    private String jspSuffix = JSP_SUFFIX_DEFAULT;
    private boolean beanBeforeJsfValidation = BEAN_BEFORE_JSF_VALIDATION_DEFAULT;
    private String facesInitPlugins;
    private boolean initializeSkipJarFacesConfigScan = INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN_DEFAULT;
    private String expressionFactory;
    private String checkIdProductionMode = CHECK_ID_PRODUCTION_MODE_DEFAULT;
    
    
    private static final boolean MYFACES_IMPL_AVAILABLE;
    private static final boolean RI_IMPL_AVAILABLE;

    static
    {
        boolean myfacesImplAvailable;
        try
        {
            ClassUtils.classForName("org.apache.myfaces.application.ApplicationImpl");
            myfacesImplAvailable = true;
        }
        catch (ClassNotFoundException e)
        {
            myfacesImplAvailable = false;
        }
        MYFACES_IMPL_AVAILABLE = myfacesImplAvailable;
        
        boolean riImplAvailable;
        try
        {
            ClassUtils.classForName("com.sun.faces.application.ApplicationImpl");
            riImplAvailable = true;
        }
        catch (ClassNotFoundException e)
        {
            riImplAvailable = false;
        }
        RI_IMPL_AVAILABLE = riImplAvailable;
    }

    public static MyfacesConfig getCurrentInstance()
    {
        return getCurrentInstance(FacesContext.getCurrentInstance().getExternalContext());
    }
    
    public static MyfacesConfig getCurrentInstance(FacesContext facesContext)
    {
        return getCurrentInstance(facesContext.getExternalContext());
    }
    
    public static MyfacesConfig getCurrentInstance(ExternalContext extCtx)
    {
        MyfacesConfig myfacesConfig = (MyfacesConfig) extCtx.getApplicationMap().get(APPLICATION_MAP_PARAM_NAME);
        if (myfacesConfig == null)
        {
            myfacesConfig = createAndInitializeMyFacesConfig(extCtx);
            extCtx.getApplicationMap().put(APPLICATION_MAP_PARAM_NAME, myfacesConfig);
        }

        return myfacesConfig;
    }
    
    public MyfacesConfig()
    {
        numberOfFlashTokensInSession = (NUMBER_OF_VIEWS_IN_SESSION_DEFAULT
                / NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT) + 1;
    }

    private static MyfacesConfig createAndInitializeMyFacesConfig(ExternalContext extCtx)
    {
        MyfacesConfig cfg = new MyfacesConfig();

        cfg.renderClearJavascriptOnButton = getBoolean(extCtx, RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON,
                RENDER_CLEAR_JAVASCRIPT_FOR_BUTTON_DEFAULT);

        cfg.renderViewStateId = getBoolean(extCtx, RENDER_VIEWSTATE_ID,
                RENDER_VIEWSTATE_ID_DEFAULT);
        
        cfg.strictXhtmlLinks = getBoolean(extCtx, STRICT_XHTML_LINKS,
                STRICT_XHTML_LINKS_DEFAULT);

        cfg.renderFormSubmitScriptInline = getBoolean(extCtx, RENDER_FORM_SUBMIT_SCRIPT_INLINE,
                RENDER_FORM_SUBMIT_SCRIPT_INLINE_DEFAULT);
        
        cfg.configRefreshPeriod = getLong(extCtx, CONFIG_REFRESH_PERIOD,
                CONFIG_REFRESH_PERIOD_DEFAULT);

        cfg.delegateFacesServlet = getString(extCtx, DELEGATE_FACES_SERVLET,
                null);
        
        String refreshTransientBuildOnPSS = getString(extCtx, REFRESH_TRANSIENT_BUILD_ON_PSS, 
                REFRESH_TRANSIENT_BUILD_ON_PSS_DEFAULT);
        if (refreshTransientBuildOnPSS == null)
        {
            cfg.refreshTransientBuildOnPSS = false;
            cfg.refreshTransientBuildOnPSSAuto = false;
        }
        else if ("auto".equalsIgnoreCase(refreshTransientBuildOnPSS))
        {
            cfg.refreshTransientBuildOnPSS = true;
            cfg.refreshTransientBuildOnPSSAuto = true;
        }
        else if (refreshTransientBuildOnPSS.equalsIgnoreCase("true") || 
                refreshTransientBuildOnPSS.equalsIgnoreCase("on") || 
                refreshTransientBuildOnPSS.equalsIgnoreCase("yes"))
        {
            cfg.refreshTransientBuildOnPSS = true;
            cfg.refreshTransientBuildOnPSSAuto = false;
        }
        else
        {
            cfg.refreshTransientBuildOnPSS = false;
            cfg.refreshTransientBuildOnPSSAuto = false;
        }
        
        cfg.refreshTransientBuildOnPSSPreserveState = getBoolean(extCtx, REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE,
                REFRESH_TRANSIENT_BUILD_ON_PSS_PRESERVE_STATE_DEFAULT);
        
        cfg.validateXML = getBoolean(extCtx, VALIDATE_XML, 
                VALIDATE_XML_DEFAULT);
        
        cfg.wrapScriptContentWithXmlCommentTag = getBoolean(extCtx, WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG, 
                WRAP_SCRIPT_CONTENT_WITH_XML_COMMENT_TAG_DEFAULT);
        
        cfg.debugPhaseListenerEnabled = getBoolean(extCtx, DEBUG_PHASE_LISTENER,
                DEBUG_PHASE_LISTENER_DEFAULT);
                
        cfg.strictJsf2CCELResolver = getBoolean(extCtx, STRICT_JSF_2_CC_EL_RESOLVER,
                STRICT_JSF_2_CC_EL_RESOLVER_DEFAULT);
        
        cfg.defaultResponseWriterContentTypeMode = getString(extCtx, DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE,
                DEFAULT_RESPONSE_WRITER_CONTENT_TYPE_MODE_DEFAULT);

        cfg.viewUniqueIdsCacheEnabled = getBoolean(extCtx, VIEW_UNIQUE_IDS_CACHE_ENABLED,
                VIEW_UNIQUE_IDS_CACHE_ENABLED_DEFAULT);

        cfg.componentUniqueIdsCacheSize = getInt(extCtx, COMPONENT_UNIQUE_IDS_CACHE_SIZE, 
                COMPONENT_UNIQUE_IDS_CACHE_SIZE_DEFAULT);
        
        cfg.supportJSPAndFacesEL = getBoolean(extCtx, SUPPORT_JSP_AND_FACES_EL,
                SUPPORT_JSP_AND_FACES_EL_DEFAULT);
                
        cfg.gaeJsfJarFiles = getString(extCtx, GAE_JSF_JAR_FILES,
                GAE_JSF_JAR_FILES_DEFAULT);
        
        cfg.gaeJsfAnnotationsJarFiles = getString(extCtx, GAE_JSF_ANNOTATIONS_JAR_FILES,
                GAE_JSF_ANNOTATIONS_JAR_FILES_DEFAULT);

        cfg.strictJsf2ViewNotFound = getBoolean(extCtx, STRICT_JSF_2_VIEW_NOT_FOUND,
                STRICT_JSF_2_VIEW_NOT_FOUND_DEFAULT);
        
        cfg.earlyFlushEnabled = getBoolean(extCtx, EARLY_FLUSH_ENABLED,
                EARLY_FLUSH_ENABLED_DEFAULT);

        cfg.strictJsf2FaceletsCompatibility = getBoolean(extCtx, STRICT_JSF_2_FACELETS_COMPATIBILITY, 
                STRICT_JSF_2_FACELETS_COMPATIBILITY_DEFAULT);
        
        cfg.renderFormViewStateAtBegin = getBoolean(extCtx, RENDER_FORM_VIEW_STATE_AT_BEGIN,
                RENDER_FORM_VIEW_STATE_AT_BEGIN_DEFAULT);
        
        cfg.flashScopeDisabled = getBoolean(extCtx, FLASH_SCOPE_DISABLED,
                FLASH_SCOPE_DISABLED_DEFAULT);
        
        cfg.strictJsf2AllowSlashLibraryName = getBoolean(extCtx, STRICT_JSF_2_ALLOW_SLASH_LIBRARY_NAME,
                STRICT_JSF_2_ALLOW_SLASH_LIBRARY_NAME_DEFAULT);
        
        try
        {
            cfg.numberOfSequentialViewsInSession = getInt(extCtx, NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION,
                    NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT);
            if (cfg.numberOfSequentialViewsInSession == null
                    || cfg.numberOfSequentialViewsInSession < 0)
            {
                Logger.getLogger(MyfacesConfig.class.getName()).severe(
                        "Configured value for " + NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION
                          + " is not valid, must be an value >= 0, using default value ("
                          + NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT);
                cfg.numberOfSequentialViewsInSession = NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT;
            }
        }
        catch (Throwable e)
        {
            Logger.getLogger(MyfacesConfig.class.getName()).log(Level.SEVERE, "Error determining the value for "
                   + NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION
                   + ", expected an integer value > 0, using default value ("
                   + NUMBER_OF_SEQUENTIAL_VIEWS_IN_SESSION_DEFAULT + "): " + e.getMessage(), e);
        }        
        try
        {
            cfg.numberOfViewsInSession = getInt(extCtx, NUMBER_OF_VIEWS_IN_SESSION,
                    NUMBER_OF_VIEWS_IN_SESSION_DEFAULT);
            if (cfg.numberOfViewsInSession == null || cfg.numberOfViewsInSession <= 0)
            {
                Logger.getLogger(MyfacesConfig.class.getName()).severe(
                        "Configured value for " + NUMBER_OF_VIEWS_IN_SESSION
                          + " is not valid, must be an value > 0, using default value ("
                          + NUMBER_OF_VIEWS_IN_SESSION_DEFAULT);
                cfg.numberOfViewsInSession = NUMBER_OF_VIEWS_IN_SESSION_DEFAULT;
            }
        }
        catch (Throwable e)
        {
            Logger.getLogger(MyfacesConfig.class.getName()).log(Level.SEVERE, "Error determining the value for "
                   + NUMBER_OF_VIEWS_IN_SESSION
                   + ", expected an integer value > 0, using default value ("
                   + NUMBER_OF_VIEWS_IN_SESSION_DEFAULT + "): " + e.getMessage(), e);
        }

        Integer numberOfFlashTokensInSessionDefault;
        if (cfg.numberOfSequentialViewsInSession != null&& cfg.numberOfSequentialViewsInSession > 0)
        {
            numberOfFlashTokensInSessionDefault = (cfg.numberOfViewsInSession
                    / cfg.numberOfSequentialViewsInSession) + 1;
        }
        else
        {
            numberOfFlashTokensInSessionDefault = cfg.numberOfViewsInSession + 1;
        }

        cfg.numberOfFlashTokensInSession = getInt(extCtx, NUMBER_OF_FLASH_TOKENS_IN_SESSION,
                numberOfFlashTokensInSessionDefault);

        cfg.numberOfFacesFlowClientWindowIdsInSession = getInt(extCtx,
                NUMBER_OF_FACES_FLOW_CLIENT_WINDOW_IDS_IN_SESSION, 
                numberOfFlashTokensInSessionDefault);
                        
        cfg.supportEL3ImportHandler = getBoolean(extCtx, SUPPORT_EL_3_IMPORT_HANDLER, 
                SUPPORT_EL_3_IMPORT_HANDLER_DEFAULT); 

        cfg.strictJsf2OriginHeaderAppPath = getBoolean(extCtx, STRICT_JSF_2_ORIGIN_HEADER_APP_PATH, 
                STRICT_JSF_2_ORIGIN_HEADER_APP_PATH_DEFAULT);

        cfg.resourceBufferSize = getInt(extCtx, RESOURCE_BUFFER_SIZE, 
                RESOURCE_BUFFER_SIZE_DEFAULT);
        
        cfg.useCdiForAnnotationScanning = getBoolean(extCtx, USE_CDI_FOR_ANNOTATION_SCANNING,
                USE_CDI_FOR_ANNOTATION_SCANNING_DEFAULT);
        
        cfg.resourceHandlerCacheEnabled = getBoolean(extCtx, RESOURCE_HANDLER_CACHE_ENABLED,
                RESOURCE_HANDLER_CACHE_ENABLED_DEFAULT);
        
        cfg.resourceHandlerCacheSize = getInt(extCtx, RESOURCE_HANDLER_CACHE_SIZE,
                RESOURCE_HANDLER_CACHE_SIZE_DEFAULT);
        
        cfg.scanPackages = getString(extCtx, SCAN_PACKAGES,
                null);
        
        String websocketEndpointPort = extCtx.getInitParameter(WEBSOCKET_ENDPOINT_PORT);
        if (websocketEndpointPort != null && !websocketEndpointPort.isEmpty())
        {
            cfg.websocketEndpointPort = Integer.valueOf(websocketEndpointPort);
        }
        
        cfg.websocketMaxIdleTimeout = getLong(extCtx, WEBSOCKET_MAX_IDLE_TIMEOUT,
                WEBSOCKET_MAX_IDLE_TIMEOUT_DEFAULT);
        
        cfg.randomKeyInWebsocketSessionToken = getString(extCtx, RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN,
                RANDOM_KEY_IN_WEBSOCKET_SESSION_TOKEN_DEFAULT);
        
        cfg.clientViewStateTimeout = getLong(extCtx, CLIENT_VIEW_STATE_TIMEOUT,
                CLIENT_VIEW_STATE_TIMEOUT_DEFAULT);
        if (cfg.clientViewStateTimeout < 0L)
        {
            cfg.clientViewStateTimeout = 0L;
        }
        
        cfg.randomKeyInViewStateSessionToken = getString(extCtx, RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_DEFAULT);
        
        cfg.randomKeyInViewStateSessionTokenLength = getInt(extCtx, RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_LENGTH_DEFAULT);
        
        cfg.randomKeyInViewStateSessionTokenSecureRandomClass = getString(extCtx,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_CLASS,
                null);
        
        cfg.randomKeyInViewStateSessionTokenSecureRandomProvider = getString(extCtx,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_PROVIDER,
                null);
        
        cfg.randomKeyInViewStateSessionTokenSecureRandomAlgorithm = getString(extCtx,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM,
                RANDOM_KEY_IN_VIEW_STATE_SESSION_TOKEN_SECURE_RANDOM_ALGORITHM_DEFAULT);
        
        cfg.randomKeyInCsrfSessionToken = getString(extCtx, RANDOM_KEY_IN_CSRF_SESSION_TOKEN,
                RANDOM_KEY_IN_CSRF_SESSION_TOKEN_DEFAULT);
        
        cfg.serializeStateInSession = getBoolean(extCtx, StateManager.SERIALIZE_SERVER_STATE_PARAM_NAME,
                false);
        
        cfg.compressStateInSession = getBoolean(extCtx, COMPRESS_STATE_IN_SESSION,
                COMPRESS_STATE_IN_SESSION_DEFAULT);
        
        cfg.useFlashScopePurgeViewsInSession = getBoolean(extCtx, USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION,
                USE_FLASH_SCOPE_PURGE_VIEWS_IN_SESSION_DEFAULT);

        cfg.autocompleteOffViewState = getBoolean(extCtx, AUTOCOMPLETE_OFF_VIEW_STATE,
                AUTOCOMPLETE_OFF_VIEW_STATE_DEFAULT);
        
        cfg.resourceMaxTimeExpires = getLong(extCtx, RESOURCE_MAX_TIME_EXPIRES,
                RESOURCE_MAX_TIME_EXPIRES_DEFAULT);
        
        cfg.lazyLoadConfigObjects = getBoolean(extCtx, LAZY_LOAD_CONFIG_OBJECTS,
                LAZY_LOAD_CONFIG_OBJECTS_DEFAULT);
        
        cfg.elResolverComparator = getString(extCtx, EL_RESOLVER_COMPARATOR,
                null);
        
        cfg.elResolverPredicate = getString(extCtx, EL_RESOLVER_PREDICATE,
                null);
        
        cfg.checkedViewIdCacheEnabled = getBoolean(extCtx, CHECKED_VIEWID_CACHE_ENABLED,
                CHECKED_VIEWID_CACHE_ENABLED_DEFAULT);
        
        cfg.checkedViewIdCacheSize = getInt(extCtx, CHECKED_VIEWID_CACHE_SIZE,
                CHECKED_VIEWID_CACHE_SIZE_DEFAULT);
        
        cfg.jspSuffix = getString(extCtx, JSP_SUFFIX,
                JSP_SUFFIX_DEFAULT);
        
        cfg.beanBeforeJsfValidation = getBoolean(extCtx, BEAN_BEFORE_JSF_VALIDATION,
                BEAN_BEFORE_JSF_VALIDATION_DEFAULT);
        
        cfg.facesInitPlugins = getString(extCtx, FACES_INIT_PLUGINS,
                null);
        
        cfg.initializeSkipJarFacesConfigScan = getBoolean(extCtx, INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN,
                INITIALIZE_SKIP_JAR_FACES_CONFIG_SCAN_DEFAULT);
        
        cfg.expressionFactory = getString(extCtx, EXPRESSION_FACTORY,
                null);
        
        cfg.checkIdProductionMode = getString(extCtx, CHECK_ID_PRODUCTION_MODE,
                CHECK_ID_PRODUCTION_MODE_DEFAULT);

        return cfg;
    }

    private static boolean getBoolean(ExternalContext externalContext, String paramName, boolean defaultValue)
    {
        String value = externalContext.getInitParameter(paramName);
        if (value == null)
        {
            return defaultValue;
        }

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes"))
        {
            return true;
        }
        else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("off") || value.equalsIgnoreCase("no"))
        {
            return false;
        }

        return defaultValue;
    }

    private static String getString(ExternalContext externalContext, String paramName, String defaultValue)
    {
        String value = externalContext.getInitParameter(paramName);
        if (value == null)
        {
            return defaultValue;
        }
        
        return value;
    }

    private static int getInt(ExternalContext externalContext, String paramName, int defaultValue)
    {
        String value = externalContext.getInitParameter(paramName);
        if (value == null)
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
        }
        return defaultValue;
    }
    
    private static long getLong(ExternalContext externalContext, String paramName, long defaultValue)
    {
       String value = externalContext.getInitParameter(paramName);
       if (value == null)
       {
           return defaultValue;
       }

        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
        }
        return defaultValue;
    }

    public boolean isMyfacesImplAvailable()
    {
        return MYFACES_IMPL_AVAILABLE;
    }

    public boolean isRiImplAvailable()
    {
        return RI_IMPL_AVAILABLE;
    }

    public boolean isStrictJsf2AllowSlashLibraryName()
    {
        return strictJsf2AllowSlashLibraryName;
    }

    public long getConfigRefreshPeriod()
    {
        return configRefreshPeriod;
    }

    public boolean isRenderViewStateId()
    {
        return renderViewStateId;
    }

    public boolean isStrictXhtmlLinks()
    {
        return strictXhtmlLinks;
    }

    public boolean isRenderClearJavascriptOnButton()
    {
        return renderClearJavascriptOnButton;
    }

    public String getDelegateFacesServlet()
    {
        return delegateFacesServlet;
    }

    public boolean isRefreshTransientBuildOnPSS()
    {
        return refreshTransientBuildOnPSS;
    }

    public boolean isRefreshTransientBuildOnPSSAuto()
    {
        return refreshTransientBuildOnPSSAuto;
    }

    public boolean isRefreshTransientBuildOnPSSPreserveState()
    {
        return refreshTransientBuildOnPSSPreserveState;
    }

    public boolean isValidateXML()
    {
        return validateXML;
    }

    public boolean isWrapScriptContentWithXmlCommentTag()
    {
        return wrapScriptContentWithXmlCommentTag;
    }

    public boolean isRenderFormSubmitScriptInline()
    {
        return renderFormSubmitScriptInline;
    }

    public boolean isDebugPhaseListenerEnabled()
    {
        return debugPhaseListenerEnabled;
    }

    public boolean isStrictJsf2CCELResolver()
    {
        return strictJsf2CCELResolver;
    }

    public String getDefaultResponseWriterContentTypeMode()
    {
        return defaultResponseWriterContentTypeMode;
    }

    public boolean isViewUniqueIdsCacheEnabled()
    {
        return viewUniqueIdsCacheEnabled;
    }

    public int getComponentUniqueIdsCacheSize()
    {
        return componentUniqueIdsCacheSize;
    }

    public boolean isSupportJSPAndFacesEL()
    {
        return supportJSPAndFacesEL;
    }

    public String getGaeJsfJarFiles()
    {
        return gaeJsfJarFiles;
    }

    public String getGaeJsfAnnotationsJarFiles()
    {
        return gaeJsfAnnotationsJarFiles;
    }

    public boolean isStrictJsf2ViewNotFound()
    {
        return strictJsf2ViewNotFound;
    }

    public boolean isEarlyFlushEnabled()
    {
        return earlyFlushEnabled;
    }

    public boolean isStrictJsf2FaceletsCompatibility()
    {
        return strictJsf2FaceletsCompatibility;
    }

    public boolean isRenderFormViewStateAtBegin()
    {
        return renderFormViewStateAtBegin;
    }

    public boolean isFlashScopeDisabled()
    {
        return flashScopeDisabled;
    }

    public Integer getNumberOfViewsInSession()
    {
        return numberOfViewsInSession;
    }

    public Integer getNumberOfSequentialViewsInSession()
    {
        return numberOfSequentialViewsInSession;
    }

    public Integer getNumberOfFlashTokensInSession()
    {
        return numberOfFlashTokensInSession;
    }

    public Integer getNumberOfFacesFlowClientWindowIdsInSession()
    {
        return numberOfFacesFlowClientWindowIdsInSession;
    }

    public boolean isSupportEL3ImportHandler()
    {
        return supportEL3ImportHandler;
    }

    public boolean isStrictJsf2OriginHeaderAppPath()
    {
        return strictJsf2OriginHeaderAppPath;
    }

    public int getResourceBufferSize()
    {
        return resourceBufferSize;
    }

    public boolean isUseCdiForAnnotationScanning()
    {
        return useCdiForAnnotationScanning;
    }

    public boolean isResourceHandlerCacheEnabled()
    {
        return resourceHandlerCacheEnabled;
    }

    public int getResourceHandlerCacheSize()
    {
        return resourceHandlerCacheSize;
    }

    public String getScanPackages()
    {
        return scanPackages;
    }

    public long getWebsocketMaxIdleTimeout()
    {
        return websocketMaxIdleTimeout;
    }   
    
    public Integer getWebsocketEndpointPort()
    {
        return websocketEndpointPort;
    }

    public long getClientViewStateTimeout()
    {
        return clientViewStateTimeout;
    }

    public String getRandomKeyInViewStateSessionToken()
    {
        return randomKeyInViewStateSessionToken;
    }

    public int getRandomKeyInViewStateSessionTokenLength()
    {
        return randomKeyInViewStateSessionTokenLength;
    }

    public String getRandomKeyInViewStateSessionTokenSecureRandomClass()
    {
        return randomKeyInViewStateSessionTokenSecureRandomClass;
    }

    public String getRandomKeyInViewStateSessionTokenSecureRandomProvider()
    {
        return randomKeyInViewStateSessionTokenSecureRandomProvider;
    }

    public String getRandomKeyInViewStateSessionTokenSecureRandomAlgorithm()
    {
        return randomKeyInViewStateSessionTokenSecureRandomAlgorithm;
    }

    public String getRandomKeyInCsrfSessionToken()
    {
        return randomKeyInCsrfSessionToken;
    }

    public boolean isSerializeStateInSession()
    {
        return serializeStateInSession;
    }

    public boolean isCompressStateInSession()
    {
        return compressStateInSession;
    }

    public boolean isUseFlashScopePurgeViewsInSession()
    {
        return useFlashScopePurgeViewsInSession;
    }

    public boolean isAutocompleteOffViewState()
    {
        return autocompleteOffViewState;
    }

    public long getResourceMaxTimeExpires()
    {
        return resourceMaxTimeExpires;
    }

    public boolean isLazyLoadConfigObjects()
    {
        return lazyLoadConfigObjects;
    }

    public String getElResolverComparator()
    {
        return elResolverComparator;
    }

    public String getElResolverPredicate()
    {
        return elResolverPredicate;
    }

    public boolean isCheckedViewIdCacheEnabled()
    {
        return checkedViewIdCacheEnabled;
    }

    public int getCheckedViewIdCacheSize()
    {
        return checkedViewIdCacheSize;
    }

    public String getJspSuffix()
    {
        return jspSuffix;
    }

    public boolean isBeanBeforeJsfValidation()
    {
        return beanBeforeJsfValidation;
    }

    public String getRandomKeyInWebsocketSessionToken()
    {
        return randomKeyInWebsocketSessionToken;
    }

    public String getFacesInitPlugins()
    {
        return facesInitPlugins;
    }

    public boolean isInitializeSkipJarFacesConfigScan()
    {
        return initializeSkipJarFacesConfigScan;
    }

    public String getExpressionFactory()
    {
        return expressionFactory;
    }

    public String getCheckIdProductionMode()
    {
        return checkIdProductionMode;
    }

    
}
