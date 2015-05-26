package com.gmail.badfalcon610.TweetOnStartUp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TweetOnStartUp extends JavaPlugin {

	static boolean first = true;
	static final String Prefix = "[TOSU]";

	static Twitter twitter;

	private TOSUCommandExecutor executor;

	@Override
	public void onEnable() {

		this.saveDefaultConfig();

		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("3JUrSISHsG4J2wA1e2MqoAc5T",
				"qdAKIvYDeRSs8N1PWmkIlsCZzVjfxAl0LAfSCnnbj0zwnbdxOp");
		RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		if (!hasAccessToken()) {
			Bukkit.getLogger().info(Prefix + "doesn't have an access token!");
			try {
				getAccessTokenR(twitter, requestToken);
			} catch (Exception e) { // TODO 自動生成された catch ブロック
				Bukkit.getLogger().info(Prefix + "twitter error");
				e.printStackTrace();
			}
		} else {
			Bukkit.getLogger().info("has an access token!");
			try {
				getAccessToken();
			} catch (Exception e) { // TODO 自動生成された catch ブロック
				Bukkit.getLogger().info(Prefix + "twitter error");
				e.printStackTrace();
			}
		}

		executor = new TOSUCommandExecutor(this, requestToken);

		// PluginManager pm = this.getServer().getPluginManager();
		// pm.registerEvents(new TOSUListener(this), this);
		getCommand("auth").setExecutor(executor);
	}

	public boolean getAccessTokenR(Twitter twitter, RequestToken requestToken)
			throws Exception {
		// このファクトリインスタンスは再利用可能でスレッドセーフです
		AccessToken accessToken = null;
		String pin = this.getConfig().getString("pin");
		if (pin != null) {
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println(Prefix
							+ "Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		if (null == accessToken) {
			System.out
					.println(Prefix
							+ "Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print(Prefix
							+ "Enter the PIN to the config.yml and run the /auth command.");
			return false;

		} else {
			// 将来の参照用に accessToken を永続化する
			storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
			tweet(twitter);

			first = false;
			return true;
		}
	}

	public void getAccessToken() throws Exception {
		// このファクトリインスタンスは再利用可能でスレッドセーフです
		TwitterFactory factory = new TwitterFactory();
		AccessToken accessToken = loadAccessToken();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer("3JUrSISHsG4J2wA1e2MqoAc5T",
				"qdAKIvYDeRSs8N1PWmkIlsCZzVjfxAl0LAfSCnnbj0zwnbdxOp");
		twitter.setOAuthAccessToken(accessToken);
		tweet(twitter);
	}

	private void tweet(Twitter twitter2) {
		try {
			List<String> rawTweetString = this.getConfig().getStringList(
					"TweetMessage");
			if (rawTweetString != null) {
				String tweetString = translate(rawTweetString);
				twitter2.updateStatus(tweetString);
				Bukkit.getLogger().info(Prefix + "tweeted succesfully!");
			}
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private String translate(List<String> strList) {
		Date date = new Date();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
		String dateStr = sdfDate.format(date);
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		String timeStr = sdfTime.format(date);
		Server server = Bukkit.getServer();
		String serverIP = getIP();
		String serverPort = ":" + server.getPort();
		String serverVersion = Bukkit.getServer().getBukkitVersion();
		String serverName = Bukkit.getServerName();
		StringBuilder sb = new StringBuilder();
		for (String str : strList) {
			str = str.replaceAll("DATE", dateStr);
			str = str.replaceAll("TIME", timeStr);
			str = str.replaceAll("SERVERIP", serverIP);
			str = str.replaceAll("SERVERPORT", serverPort);
			str = str.replaceAll("VERSION", serverVersion);
			str = str.replaceAll("SERVERNAME", serverName);
			sb.append(str + "\n");
		}
		return sb.toString();

	}

	private void storeAccessToken(long l, AccessToken accessToken) {
		FileConfiguration config = this.getConfig();
		config.set("token", accessToken.getToken());
		config.set("tokenSecret", accessToken.getTokenSecret());

		saveConfig();

		// accessToken.getToken() を保存
		// accessToken.getTokenSecret() を保存
	}

	private AccessToken loadAccessToken() {
		String token = this.getConfig().getString("token");
		String tokenSecret = this.getConfig().getString("tokenSecret");
		return new AccessToken(token, tokenSecret);
	}

	private boolean hasAccessToken() {
		boolean b = true;
		FileConfiguration config = this.getConfig();
		if (!config.contains("token")) {
			b = false;
		}
		if (!config.contains("tokenSecret")) {
			b = false;
		}
		return b;
	}

	static String getIP() {
		URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						whatismyip.openStream()));
				String ip = in.readLine();
				return ip;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

}
