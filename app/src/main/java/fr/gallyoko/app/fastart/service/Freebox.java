package fr.gallyoko.app.fastart.service;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fr.gallyoko.app.fastart.bdd.entity.ConfigEntity;
import fr.gallyoko.app.fastart.bdd.entity.ContentTypeEntity;
import fr.gallyoko.app.fastart.bdd.repository.ConfigRepository;

public class Freebox {
    private Context context;
    private Timer timerTrackId;
    private TimerTask timerTaskTrackId;
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private String appId = "";
    private String appName = "";
    private String appVersion = "";
    private String deviceName = "";
    private String urlApi = "";
    private String routeAuth = "";
    private String routeTracking = "";
    private String routeLogin = "";
    private String routeLoginSession = "";
    private String routeAirMediaConfig = "";

    public Freebox(Context context) {
        this.context = context;
        this.appId = "fr.freebox.fastart";
        this.appName = "FaStart";
        this.appVersion = "0.3";
        this.deviceName = "FwedPhone";
        this.setUrlFromEnv("prod");
    }

    private void setUrlFromEnv(String env) {
        if (env.equals("prod")) {
            this.urlApi = "http://mafreebox.freebox.fr/api/v4";
            this.routeAuth = "/login/authorize/";
            this.routeTracking = "/login/authorize/";
            this.routeLogin = "/login";
            this.routeLoginSession = "/login/session";
            this.routeAirMediaConfig = "/airmedia/config/";
        } else {
            this.urlApi = "http://172.20.0.2:8091";
            //this.urlApi = "http://83.157.150.119:9394";
            this.routeAuth = "/freebox/authorization";
            this.routeTracking = "/freebox/tracking/";
            this.routeLogin = "/freebox/login";
            this.routeLoginSession = "/freebox/login/session";
        }
    }

    public void authFreebox() {
        final Context context = this.context;
        Api apiService = new Api(context,
                this.urlApi + this.routeAuth,
                "POST",
                "{'app_id' : '"+this.appId+"','app_name' : '"+this.appName+"','app_version' : '"+this.appVersion+"','device_name' : '"+this.deviceName+"'}", null,
                new ApiResponse(){
                    @Override
                    public void getResponse(JSONObject output) {
                        String message = "";
                        try {
                            if (output.getBoolean("success")) {
                                message = "Merci de confirmer sur la freebox";
                                JSONObject result = output.getJSONObject("result");
                                String appToken = result.getString("app_token");
                                ConfigRepository configRepository = new ConfigRepository(context);
                                configRepository.open();
                                ConfigEntity configEntity;
                                if (configRepository.getByCode("FREEBOX_TOKEN")!=null) {
                                    configEntity = configRepository.getByCode("FREEBOX_TOKEN");
                                    configEntity.setValue(appToken);
                                    configRepository.update(configEntity.getId(), configEntity);
                                } else {
                                    configEntity = new ConfigEntity("FREEBOX_TOKEN", appToken);
                                    configRepository.insert(configEntity);
                                }
                                configRepository.close();
                                final String trackId = result.getString("track_id");
                                timerTrackId = new Timer();
                                timerTaskTrackId = new TimerTask() {
                                    @Override
                                    public void run() {
                                        //do something
                                        checkAuthFreebox(trackId);
                                    }
                                };
                                timerTrackId.schedule(timerTaskTrackId,3000, 3000);
                            } else {
                                message = "Erreur lors de l'appel";
                            }
                        } catch (Exception e) {
                            message = "Erreur de parsing";
                        } finally {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        apiService.exec();
    }

    private void checkAuthFreebox(String trackId) {
        final Context context = this.context;
        Api apiService = new Api(context,
                this.urlApi + this.routeTracking + trackId,
                "GET", "", null,
                new ApiResponse(){
                    @Override
                    public void getResponse(JSONObject output) {
                        String message = "";
                        try {
                            if (output.getBoolean("success")) {
                                JSONObject result = output.getJSONObject("result");
                                String status = result.getString("status");
                                if (status.equals("granted")) {
                                    timerTaskTrackId.cancel();
                                    timerTrackId.cancel();
                                    ConfigRepository configRepository = new ConfigRepository(context);
                                    configRepository.open();
                                    ConfigEntity configEntity = configRepository.getByCode("FREEBOX_STATUS");
                                    configEntity.setValue("granted");
                                    configRepository.update(configEntity.getId(), configEntity);
                                    configRepository.close();
                                    challenge();
                                }
                            } else {
                                message = "Erreur lors de l'appel du tracking";
                            }
                        } catch (Exception e) {
                            message = "Erreur de parsing du tracking";
                        } finally {
                            if (!message.equals("")) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                timerTaskTrackId.cancel();
                                timerTrackId.cancel();
                            }
                        }

                    }
                });
        apiService.exec();
    }

    public void challenge() {
        final Context context = this.context;
        Api apiService = new Api(context,
                this.urlApi + this.routeLogin,
                "GET", "", null,
                new ApiResponse(){
                    @Override
                    public void getResponse(JSONObject output) {
                        String message = "";
                        try {
                            if (output.getBoolean("success")) {
                                JSONObject result = output.getJSONObject("result");
                                String challenge = result.getString("challenge");
                                loginSession(challenge);
                            } else {
                                message = "Erreur lors de l'appel du challenge";
                            }
                        } catch (Exception e) {
                            message = "Erreur de parsing du challenge";
                        } finally {
                            if (!message.equals("")) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        apiService.exec();
    }

    private void loginSession(String challenge) throws Exception {
        final Context context = this.context;
        ConfigRepository configRepository = new ConfigRepository(context);
        configRepository.open();
        ConfigEntity configEntity = configRepository.getByCode("FREEBOX_TOKEN");
        configRepository.close();
        String password = calculateRFC2104HMAC(challenge, configEntity.getValue());
        Api apiService = new Api(context,
                this.urlApi + this.routeLoginSession,
                "POST", "{'app_id':'"+this.appId+"', 'password':'"+password+"'}", null,
                new ApiResponse(){
                    @Override
                    public void getResponse(JSONObject output) {
                        String message = "";
                        try {
                            if (output.getBoolean("success")) {
                                JSONObject result = output.getJSONObject("result");
                                String token = result.getString("session_token");
                                ConfigRepository configRepository = new ConfigRepository(context);
                                configRepository.open();
                                ConfigEntity configEntity;
                                if (configRepository.getByCode("FREEBOX_SESSION_TOKEN")!=null) {
                                    configEntity = configRepository.getByCode("FREEBOX_SESSION_TOKEN");
                                    configEntity.setValue(token);
                                    configRepository.update(configEntity.getId(), configEntity);
                                } else {
                                    configEntity = new ConfigEntity("FREEBOX_SESSION_TOKEN", token);
                                    configRepository.insert(configEntity);
                                }
                                configRepository.close();
                            } else {
                                message = "Erreur lors de l'appel du loginSession";
                            }
                        } catch (Exception e) {
                            message = "Erreur de parsing du loginSession";
                        } finally {
                            if (!message.equals("")) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        apiService.exec();
    }

    public void getAirMediaConfig() {
        final Context context = this.context;
        ConfigRepository configRepository = new ConfigRepository(context);
        configRepository.open();
        if (configRepository.getByCode("FREEBOX_SESSION_TOKEN")!=null) {
            ConfigEntity configEntity = configRepository.getByCode("FREEBOX_SESSION_TOKEN");
            String tokenSession = configEntity.getValue();
            configRepository.close();
            ArrayList<ContentTypeEntity> contentTypes = new ArrayList<>();
            ContentTypeEntity contentTypeEntity1 = new ContentTypeEntity();
            contentTypeEntity1.setName("CONTENT_TYPE");
            contentTypeEntity1.setValue("application/x-www-form-urlencoded");
            contentTypes.add(contentTypeEntity1);
            ContentTypeEntity contentTypeEntity2 = new ContentTypeEntity();
            contentTypeEntity2.setName("FREEBOX_APP_AUTH");
            contentTypeEntity2.setValue(tokenSession);
            contentTypes.add(contentTypeEntity2);
            Api apiService = new Api(context,
                    this.urlApi + this.routeAirMediaConfig,
                    "GET", "", contentTypes,
                    new ApiResponse(){
                        @Override
                        public void getResponse(JSONObject output) {
                            String message = "";
                            try {
                                if (output.getBoolean("success")) {
                                    JSONObject result = output.getJSONObject("result");
                                    boolean enabled = result.getBoolean("enabled");
                                    if (enabled) {
                                        Log.i("ENABLED", "YES");
                                    } else {
                                        Log.i("ENABLED", "NO");
                                    }
                                } else {
                                    message = "Erreur lors de l'appel de getAirMediaConfig";
                                }
                            } catch (Exception e) {
                                message = "Erreur de parsing de getAirMediaConfig";
                            } finally {
                                if (!message.equals("")) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
            apiService.exec();
        } else {
            Toast.makeText(context, "Erreur de token", Toast.LENGTH_SHORT).show();
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }
}
