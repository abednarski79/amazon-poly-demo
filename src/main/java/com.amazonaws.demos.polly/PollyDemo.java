package com.amazonaws.demos.polly;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class PollyDemo {

    public static void main(String args[]) throws Exception {
        PollyDemo demo = new PollyDemo();
        InputStream audioStream = demo.getAudioInputStream();
        demo.storeInputStreamToTemporaryFile(audioStream);
    }

    private InputStream getAudioInputStream() {
        System.out.println("Creating amazon polly client.");
        AmazonPollyClient polly = new AmazonPollyClient(new DefaultAWSCredentialsProviderChain(),
                new ClientConfiguration());
        Region region = Region.getRegion(Regions.EU_WEST_1);
        polly.setRegion(region);
        System.out.println("Creating describe voices request.");
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
        System.out.println("Synchronously ask Amazon Polly to describe available TTS voices.");
        DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
        Voice voice = describeVoicesResult.getVoices().get(0);
        System.out.println("Creating Amazon Polly TTS request.");
        SynthesizeSpeechRequest synthReq =
                new SynthesizeSpeechRequest().withText("This is amazon polly test application.").withVoiceId(voice.getId())
                        .withOutputFormat(OutputFormat.fromValue("mp3"));
        System.out.println("Requesting Amazon Polly TTS service.");
        SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);
        System.out.println("Amazon Polly service call finished.");
        InputStream audioStream = synthRes.getAudioStream();
        System.out.println("Returning audio stream.");
        return audioStream;
    }

    private void storeInputStreamToTemporaryFile(InputStream fileStreamToStore) {
        System.out.println("Storing file stream to temporary file.");
        String tmpFilePath = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".tmp";
        System.out.println("Temporary file name: " + tmpFilePath);
        try {
            FileUtils.copyToFile(fileStreamToStore, new File(tmpFilePath));
        } catch (IOException cantReadAudioStreamException) {
            throw new RuntimeException("Can't copy input stream to file: ", cantReadAudioStreamException);
        } finally {
            if (fileStreamToStore != null) {
                try {
                    // outputStream.flush();
                    fileStreamToStore.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}