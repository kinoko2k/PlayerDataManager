package net.kinoko2k.pdm.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class DiscordWebhook {
    private final String webhookUrl;
    private final OkHttpClient client;

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.client = new OkHttpClient();
    }

    public void sendMessage(String content) {
        JSONObject json = new JSONObject();
        json.put("content", content);

        RequestBody body = RequestBody.create(
            json.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(webhookUrl)
            .post(body)
            .build();

        try {
            client.newCall(request).execute().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}