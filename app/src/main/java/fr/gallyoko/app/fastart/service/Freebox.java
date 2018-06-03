package fr.gallyoko.app.fastart.service;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fr.gallyoko.app.fastart.bdd.entity.ConfigEntity;
import fr.gallyoko.app.fastart.bdd.repository.ConfigRepository;

public class Freebox {
    private Context context;
    private Timer timerTrackId;
    private TimerTask timerTaskTrackId;
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public Freebox(Context context) {
        this.context = context;
    }

    public void authFreebox() {
        final Context context = this.context;
        Api apiService = new Api(context,
                "http://172.20.0.2:8091/freebox/authorization",
                "POST",
                "{'app_id' : 'fr.freebox.fastart','app_name' : 'FaStart','app_version' : '0.3','device_name' : 'FwedPhone'}",
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
                "http://172.20.0.2:8091/freebox/tracking/"+trackId,
                "GET", "",
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
                "http://172.20.0.2:8091/freebox/login",
                "GET", "",
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

    private void loginSession(String challenge) throws Exception {
        final Context context = this.context;
        ConfigRepository configRepository = new ConfigRepository(context);
        configRepository.open();
        ConfigEntity configEntity = configRepository.getByCode("FREEBOX_TOKEN");
        configRepository.close();
        String password = calculateRFC2104HMAC(challenge, configEntity.getValue());
        Api apiService = new Api(context,
                "http://172.20.0.2:8091/freebox/login/session",
                "POST", "{'app_id':'fr.freebox.fastart', 'password':'"+password+"'}",
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
}
