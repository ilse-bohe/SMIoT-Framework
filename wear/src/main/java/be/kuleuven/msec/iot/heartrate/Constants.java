/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.kuleuven.msec.iot.heartrate;

import java.util.concurrent.TimeUnit;

/**
 * A collection of constants that is shared between the wearable and handset apps.
 */
public class Constants {

    public final static String MONITOR_HEARTRATE = "monitor_heartrate";
    public final static String UNMONITOR_HEARTRATE = "unmonitor_heartrate";
    public final static String REQUEST_HEARTRATE = "request_heartrate";
    public final static String REACHABLE = "reachable";



    private Constants() {}
}