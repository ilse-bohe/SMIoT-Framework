package be.kuleuven.msec.iot.iotframework.implementations.loudnesssensors.androiddevice;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LoudnessSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class AndroidLoudnessConnector extends VirtualIoTConnector {

    private Map<String, String> settings;

    final String TAG = "AndroidLoudnessConnector";


    private AndroidLoudnessConnector thisConnector;
    private MyMediaRecorder mRecorder;

    private Boolean monitoring =true;


    public AndroidLoudnessConnector(String systemID, Map<String, String> settings){
        super(systemID);
        this.settings=settings;
        thisConnector = this;
    }


    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        String systemID= null;

        for (JSMDevice dev :devices) {
            if (dev.getModel().equals(Model_constants.MODEL_ANDROID) && dev.getType().equals("loudness_sensor") ){
                systemID=dev.getSystemID();
            }
        }

        LoudnessSensor temp = new AndroidLoudnessSensor(systemID, thisConnector);
        connectedDevices.add(temp);

        orc.onSuccess(true);
    }


    @Override
    public void initialize(OnRequestCompleted orc) {
        //nothing needs to be done here
        orc.onSuccess(true);
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {

    }

    @Override
    public void monitorReachability(OnEventOccurred<Boolean> oeo) {

    }

    @Override
    public void connect(OnRequestCompleted<Boolean> orc) {

    }

    @Override
    public void disconnect(OnRequestCompleted<Boolean> orc) {

    }



  /*  public void requestLoudness(OnRequestCompleted<Double> orc) {
        mRecorder = new MyMediaRecorder();
        File file = createFile("temp.amr");
        Thread thread;
        if (file != null) {
            mRecorder.setMyRecAudioFile(file);
            if (mRecorder.startRecorder()) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            float volume= 0;
                            while(volume<1 || volume> (1000000-1)){
                                volume=mRecorder.getMaxAmplitude();
                                Thread.sleep(200);
                            }
                            orc.onSuccess((20 * (double) (Math.log10(volume))));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            else {
                orc.onFailure(new Exception("unable to start recorder"));
            }
            //startRecord(file, orc);
        } else {
            orc.onFailure(new Exception("recorder file is null"));
        }
    }*/

    double requestLoudness(OnRequestCompleted<Double> orc) {
        mRecorder = new MyMediaRecorder();
        File file = createFile();
        Thread thread;

        mRecorder.setMyRecAudioFile(file);
        if (mRecorder.startRecorder()) {
            try {
                float volume= 0;
                while(volume<1 || volume> (1000000-1)){
                    volume=mRecorder.getMaxAmplitude();

                    Thread.sleep(200);

                }
                return (20 * (double) (Math.log10(volume)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            orc.onFailure(new Exception("unable to start recorder"));
        }
        //startRecord(file, orc);

        return 0;
    }


    Observable monitorLoudness(){
            return Observable.create(new ObservableOnSubscribe<Double>() {
                @Override
                public void subscribe(ObservableEmitter emitter) {

                    try {

                        /*
                         * The emitter can be used to emit each list item
                         * to the subscriber.
                         *
                         * */
                        mRecorder = new MyMediaRecorder();
                        File file = createFile();
                        if (file != null) {
                            mRecorder.setMyRecAudioFile(file);
                            if (mRecorder.startRecorder()) {
                                monitoring=true;
                                while (monitoring) {
                                    try {

                                        float volume = mRecorder.getMaxAmplitude();  //Get the sound pressure value
                                        if(volume > 0 && volume < 1000000) {
                                            emitter.onNext (20 * (double)(Math.log10(volume)));
                                        }
                                        Thread.sleep(500);

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //monitoringThread.start();
                            }
                            else {
                                emitter.onError(new Exception("unable to start recorder"));
                            }
                        } else {
                            emitter.onError(new Exception("recorder file is null"));
                        }

                        /*
                         * Once all the items in the list are emitted,
                         * we can call complete stating that no more items
                         * are to be emitted.
                         *
                         * */
                        emitter.onComplete();

                    } catch (Exception e) {

                        /*
                         * If an error occurs in the process,
                         * we can call error.
                         *
                         * */
                        emitter.onError(e);
                    }
                }
            });

    }


    /*public double monitorLoudness(OnEventOccurred<Double> oeo) {
        mRecorder = new MyMediaRecorder();
        File file = createFile("temp.amr");
        if (file != null) {
            mRecorder.setMyRecAudioFile(file);
            if (mRecorder.startRecorder()) {
                monitoring=true;
                while (monitoring) {
                    try {

                        float volume = mRecorder.getMaxAmplitude();  //Get the sound pressure value
                        if(volume > 0 && volume < 1000000) {
                           return (20 * (double)(Math.log10(volume)));
                        }
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //monitoringThread.start();
            }
            else {
                oeo.onErrorOccurred(new Exception("unable to start recorder"));
            }
        } else {
            oeo.onErrorOccurred(new Exception("recorder file is null"));
        }
        return 0;
    }*/

    void unmonitorLoudness() {
        monitoring=false;
        mRecorder.delete();
    }

    private File createFile() {
        final String LOCAL = "SMIoT";
        final String LOCAL_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;

        /**
         * Recording file directory
         */
        final String REC_PATH = LOCAL_PATH + LOCAL + File.separator;

        File dirRootFile = new File(LOCAL_PATH);
        if (!dirRootFile.exists()) {
            dirRootFile.mkdirs();
        }
        File recFile = new File(REC_PATH);
        if (!recFile.exists()) {
            recFile.mkdirs();
        }





        File myCaptureFile = new File(REC_PATH + "temp.amr");
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }



    private class MyMediaRecorder {
        final String TAG ="MyMediaRecorder";
        File myRecAudioFile ;
        private MediaRecorder mMediaRecorder ;
        boolean isRecording = false ;

        float getMaxAmplitude() {
            if (mMediaRecorder != null) {
                try {
                    return mMediaRecorder.getMaxAmplitude();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return 0;
                }
            } else {
                Log.w(TAG, "mMediaRecorder is null");
                return 5;
            }
        }

        public File getMyRecAudioFile() {
            return myRecAudioFile;
        }

        void setMyRecAudioFile(File myRecAudioFile) {
            this.myRecAudioFile = myRecAudioFile;
        }

        /**
         * Recording
         * @return Whether to start recording successfully
         */
        boolean startRecorder(){
            if (myRecAudioFile == null) {
                Log.w(TAG, "myRecAudioFile is null");
                return false;
            }
            try {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                isRecording = true;
                return true;
            } catch(IOException exception) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                isRecording = false ;
                exception.printStackTrace();
            }catch(IllegalStateException e){
                stopRecording();
                e.printStackTrace();
                isRecording = false ;
            }
            return false;
        }




        void stopRecording() {
            if (mMediaRecorder != null){
                if(isRecording){
                    try{
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                mMediaRecorder = null;
                isRecording = false ;
            }
        }



        void delete() {
            stopRecording();
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
                myRecAudioFile = null;
            }
        }
    }
}
