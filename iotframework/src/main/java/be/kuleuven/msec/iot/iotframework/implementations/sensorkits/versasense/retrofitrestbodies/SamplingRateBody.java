package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.retrofitrestbodies;

/**
 * Created by ilsebohe on 06/12/2017.
 */

public class SamplingRateBody {

    String sampling_rate ;

    public SamplingRateBody(int sampling_rate){
        this.sampling_rate = Integer.toString(sampling_rate);
}
}
