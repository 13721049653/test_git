package org.ezplatform.workflow.web.client;

import org.ezplatform.workflow.util.AESUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value= {"classpath:/httpConfig.properties"})
@ConfigurationProperties(prefix = "spring.resttemplate")
public class HttpClientProperties {
	private String secretKeyStr = "4FE2CDF779F836D63774C43E44_=+";
    /**
     * 是否使用httpclient连接池
     */
    private boolean useHttpClientPool = true;

    /**
     * 从连接池中获得一个connection的超时时间
     */
    private int connectionRequestTimeout = 3000;

    /**
     * 建立连接超时时间
     */
    private int connectTimeout = 3000;

    /**
     * 建立连接后读取返回数据的超时时间
     */
    private int readTimeout = 5000;

    /**
     * 连接池的最大连接数，0代表不限
     */
    private int maxTotalConnect = 128;

    /**
     * 每个路由的最大连接数
     */
    private int maxConnectPerRoute = 32;
    private int maxRoute=20;
    /**
     * 金蝶服务器地址
     */
    private String KingUrl="";
    
    private String KingIp="";
    private String KingPort="";
    private String acctID="";
    private String username="";
    private String password="";
    private String lcid="";
    private String appId="";
    private String appSecret="";
    private String path;
    
    private String idsUrl="";
    private String idsIp="";
    private String idsPort="";
    
    
   
    
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String baseUrl;
    private String baseIp;
    private String basePort;

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxTotalConnect() {
        return maxTotalConnect;
    }

    public void setMaxTotalConnect(int maxTotalConnect) {
        this.maxTotalConnect = maxTotalConnect;
    }

    public int getMaxConnectPerRoute() {
        return maxConnectPerRoute;
    }

    public void setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
    }

    
    
    public int getMaxRoute() {
		return maxRoute;
	}

	public void setMaxRoute(int maxRoute) {
		this.maxRoute = maxRoute;
	}

	public boolean isUseHttpClientPool() {
        return useHttpClientPool;
    }

    public void setUseHttpClientPool(boolean useHttpClientPool) {
        this.useHttpClientPool = useHttpClientPool;
    }

    
	

	public String getKingIp() {
		return KingIp;
	}

	public void setKingIp(String kingIp) {
		KingIp = kingIp;
	}

	public String getKingPort() {
		return KingPort;
	}

	public void setKingPort(String kingPort) {
		KingPort = kingPort;
	}

	public String getKingUrl() {
		return KingUrl;
	}

	public void setKingUrl(String kingUrl) {
		KingUrl = kingUrl;
	}

	public String getAcctID() {
		return acctID;
	}

	public void setAcctID(String acctID) {
		this.acctID = acctID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = AESUtil.decode(secretKeyStr, password) ;;
	}

	public String getLcid() {
		return lcid;
	}

	public void setLcid(String lcid) {
		this.lcid = lcid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIdsUrl() {
		return idsUrl;
	}

	public void setIdsUrl(String idsUrl) {
		this.idsUrl = idsUrl;
	}

	public String getIdsIp() {
		return idsIp;
	}

	public void setIdsIp(String idsIp) {
		this.idsIp = idsIp;
	}

	public String getIdsPort() {
		return idsPort;
	}

	public void setIdsPort(String idsPort) {
		this.idsPort = idsPort;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseIp() {
		return baseIp;
	}

	public void setBaseIp(String baseIp) {
		this.baseIp = baseIp;
	}

	public String getBasePort() {
		return basePort;
	}

	public void setBasePort(String basePort) {
		this.basePort = basePort;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
    
	
	
	
    
}

