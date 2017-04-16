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
        String text = "Gallace is an artist who works within the self-imposed confines of a rigorously limited scale and subject matter. She is a painter of small, unpeopled landscapes in which a modest number of elements a house, a barn, a boat; bushes, grass, sky recur with a quietly mesmerising insistence. In focusing on a particularly favoured motif, the idealised form of a windowless white New England cottage, Gallace succeeds in isolating something universally familiar yet utterly mysterious. Though instantly recognisable as a work by Gallace, each individual painting is a unique rumination on stillness and structure.";
        String outputFilePath = demo.getAudioInputStreamAndStoreInputStreamToTemporaryFile(text);
        System.out.println("File stored in: " + outputFilePath);
    }

    private String getAudioInputStreamAndStoreInputStreamToTemporaryFile(String text) {
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
                new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
                        .withOutputFormat(OutputFormat.fromValue("mp3"));
        System.out.println("Requesting Amazon Polly TTS service.");
        SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);
        System.out.println("Amazon Polly service call finished.");
        InputStream audioStream = synthRes.getAudioStream();
        System.out.println("Returning audio stream.");
        System.out.println("Storing file stream to temporary file.");
        String tmpFilePath = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".tmp";
        System.out.println("Temporary file name: " + tmpFilePath);
        try {
            FileUtils.copyToFile(audioStream, new File(tmpFilePath));
            System.out.println("File copied.");
        } catch (IOException cantReadAudioStreamException) {
            System.err.println("Can't copy input stream to file.");
            cantReadAudioStreamException.printStackTrace();
        } finally {
            if (audioStream != null) {
                try {
                    // outputStream.flush();
                    audioStream.close();
                    System.out.println("Audio stream closed.");
                } catch (IOException e) {
                    System.err.println("Can't close audio stream.");
                    e.printStackTrace();
                }
            }
        }
        return tmpFilePath;
    }
}