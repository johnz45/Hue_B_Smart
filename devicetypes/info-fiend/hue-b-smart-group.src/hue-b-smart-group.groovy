/**
 *  Hue B Smart Group
 *
 *  Copyright 2016 Anthony Pastor
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *	Version 1.1 -- fixed Hue and Saturation updates, added hue and saturation tiles & sliders, added Flash tile, conformed device layout with Hue Bulb DTH, 
 * 					and added setHuefrom100 function (for using a number 1-100 in CoRE - instead of the 1-360 that CoRE normally uses)
 * 
 *	Version 1.2 -- conformed DTHs
 *
 *	Version 1.2b -- Fixed updateStatus() error; attribute colorTemp is now colorTemperature - changing colorTemperature no longer turns on device
 */
 
 
metadata {
	definition (name: "Hue B Smart Group", namespace: "info_fiend", author: "Anthony Pastor") {
		capability "Switch Level"
		capability "Actuator"
		capability "Color Control"
        capability "Color Temperature"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        capability "Configuration"
        
        command "setAdjustedColor"
        command "reset"
        command "refresh"
        command "updateStatus"
        command "flash"
        command "flash_off"
		command "ttUp"
        command "ttDown"
        command "setColorTemperature"
        command "setTransitionTime"
        command "colorloopOn"
        command "colorloopOff"
        command "getHextoXY"
        command "setHue"
        command "setHueUsing100"               
        command "setSaturation"
        command "sendToHub"
        command "setLevel"

        
        attribute "lights", "STRING"       
		attribute "transitionTime", "NUMBER"
        attribute "colorTemperature", "number"
		attribute "bri", "number"
		attribute "saturation", "number"
		attribute "hue", "number"
		attribute "on", "string"
		attribute "colormode", "enum", ["XY", "CT", "HS"]
		attribute "effect", "enum", ["none", "colorloop"]
        attribute "groupID", "string"
        attribute "host", "string"
        attribute "username", "string"
        
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		multiAttributeTile(name:"rich-control", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-multi", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-multi", backgroundColor:"#C6C7CC", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-multi", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-multi", backgroundColor:"#C6C7CC", nextState:"turningOn"
			}
            
            
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel", range:"(0..100)"
            }

			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setAdjustedColor"
			}
		}

		/* reset / refresh */	
		standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"Reset Color", action:"reset", icon:"st.lights.philips.hue-multi"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        /* Hue & Saturation */
		valueTile("valueHue", "device.hue", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "hue", label: 'Hue: ${currentValue}'
        }
        controlTile("hue", "device.hue", "slider", inactiveLabel: false,  width: 4, height: 1) { 
        	state "setHue", action:"setHue"
		}
		valueTile("valueSat", "device.saturation", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "saturation", label: 'Sat: ${currentValue}'
        }
        controlTile("saturation", "device.saturation", "slider", inactiveLabel: false,  width: 4, height: 1) { 
        	state "setSaturation", action:"setSaturation"
		}
        
        /* Color Temperature */
		valueTile("valueCT", "device.colorTemperature", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "colorTemperature", label: 'Color Temp:  ${currentValue}'
        }
        controlTile("colorTemperature", "device.colorTemperature", "slider", inactiveLabel: false,  width: 4, height: 1, range:"(2200..6500)") { 
        	state "setCT", action:"setColorTemperature"
		}
        
        /* Flash / Alert */
		standardTile("flash", "device.flash", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"Flash", action:"flash", icon:"st.lights.philips.hue-multi"
		}
        
        /* transition time */
		valueTile("ttlabel", "transitionTime", decoration: "flat", width: 2, height: 1) {
			state "default", label:'Transition Time: ${currentValue}00ms'
		}
		valueTile("ttdown", "device.transitionTime", decoration: "flat", width: 2, height: 1) {
			state "default", label: "TT -100ms", action:"ttDown"
		}
		valueTile("ttup", "device.transitionTime", decoration: "flat", width: 2, height: 1) {
			state "default", label:"TT +100ms", action:"ttUp"
		}
        
        /* misc */
        valueTile("lights", "device.lights", inactiveLabel: false, decoration: "flat", width: 5, height: 1) {
			state "default", label: 'Lights: ${currentValue}'
        }
        valueTile("groupID", "device.groupID", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "default", label: 'GroupID: ${currentValue}'
		}
		valueTile("colormode", "device.colormode", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
			state "default", label: 'Colormode: ${currentValue}'
		}
        
        
        valueTile("host", "device.host", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label: 'Host: ${currentValue}'
        }
        valueTile("username", "device.username", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label: 'User: ${currentValue}'
        }

		standardTile("toggleColorloop", "device.effect", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
			state "colorloop", label:"On", action:"colorloopOff", nextState: "updating", icon:"https://raw.githubusercontent.com/infofiend/Hue-Lights-Groups-Scenes/master/smartapp-icons/hue/png/colorloop-on.png"
            state "none", label:"Off", action:"colorloopOn", nextState: "updating", icon:"https://raw.githubusercontent.com/infofiend/Hue-Lights-Groups-Scenes/master/smartapp-icons/hue/png/colorloop-off.png"
            state "updating", label:"Working", icon: "st.secondary.secondary"
		}
	}
	main(["rich-control"])
	details(["rich-control","colormode","groupID","valueHue","hue","valueSat","saturation","valueCT","colorTemperature","ttlabel","ttdown","ttup","lights","toggleColorloop","flash","reset","refresh"]) //  "host", "username", 
}

private configure() {		
    def commandData = parent.getCommandData(device.deviceNetworkId)
    log.debug "${commandData = commandData}"
    sendEvent(name: "groupID", value: commandData.deviceId, displayed:true, isStateChange: true)
    sendEvent(name: "host", value: commandData.ip, displayed:false, isStateChange: true)
    sendEvent(name: "username", value: commandData.username, displayed:false, isStateChange: true)
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}


def ttUp() {
	log.trace "Hue B Smart Group: ttUp(): "
	def tt = this.device.currentValue("transitionTime") ?: 0
    if (tt == null) { tt = 4 }
    sendEvent(name: "transitionTime", value: tt + 1)
}

def ttDown() {
	log.trace "Hue B Smart Group: ttDown(): "
	def tt = this.device.currentValue("transitionTime") ?: 0
    tt = tt - 1
    if (tt < 0) { tt = 0 }
    sendEvent(name: "transitionTime", value: tt)
}


/** 
 * capability.switchLevel 
 **/
def setLevel(inLevel) {
	log.trace "Hue B Smart Group: setLevel ( ${inLevel} ): "
	def level = parent.scaleLevel(inLevel, true, 254)
	log.debug "Setting level to ${level}."

    def commandData = parent.getCommandData(device.deviceNetworkId)
    def tt = this.device.currentValue("transitionTime") ?: 0
    
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: true, bri: level, transitiontime: tt]
		])
	)    
}

/**
 * capability.colorControl 
 **/
def sendToHub(values) {
	log.trace "Hue B Smart Group: sendToHub ( ${values} ): "
    
	def validValues = [:]
	def commandData = parent.getCommandData(device.deviceNetworkId)       

        def sendBody = [:]

	if (values.level) {
    	def bri = values.level 
    	validValues.bri = parent.scaleLevel(bri, true, 254)
        sendBody["bri"] = validValues.bri
		if (values.level > 0) { 
        	sendBody["on"] = true
        } else {
        	sendBody["on"] = false
		}            
	}

	if (values.switch == "off" ) {
    	sendBody["on"] = false
       
    } else if (values.switch == "on") {
		sendBody["on"] = true
       
	}
        
    sendBody["transitiontime"] = device.currentValue("transitionTime") as Integer ?: 0
    
    if (values.hex != null) {
		if (values.hex ==~ /^\#([A-Fa-f0-9]){6}$/) {
			validValues.xy = getHextoXY(values.hex)
            sendBody["xy"] = validValues.xy
		} else {
            log.warn "$values.hex is not a valid color"
        }
	}

    if (validValues.xy) {
    
		log.debug "XY value found.  Sending ${sendBody} " 

		parent.sendHubCommand(new physicalgraph.device.HubAction(
    		[
        		method: "PUT",
				path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
		        headers: [
		        	host: "${commandData.ip}"
				],
	        	body: sendBody 	
			])
		)
		sendEvent(name: "colormode", value: "XY", isStateChange: true) 
        sendEvent(name: "hue", value: values.hue as Integer) 
        sendEvent(name: "saturation", value: values.saturation as Integer, isStateChange: true) 
        
	} else {
    
    	log.trace "sendToHub: no XY values, so using Hue & Saturation."
		def hue = values.hue ?: this.device.currentValue("hue")
    	validValues.hue = parent.scaleLevel(hue, true, 65535)
		sendBody["hue"] = validValues.hue
		def sat = values.saturation ?: this.device.currentValue("saturation")
	    validValues.saturation = parent.scaleLevel(sat, true, 254)
		sendBody["sat"] = validValues.saturation
        
		log.debug "Sending ${sendBody} "

		parent.sendHubCommand(new physicalgraph.device.HubAction(
    		[
    	    	method: "PUT",
				path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        	headers: [
	    	    	host: "${commandData.ip}"
				],
		        body: sendBody 
			])
		)    
		sendEvent(name: "colormode", value: "HS") //, isStateChange: true) 
 //       sendEvent(name: "switch", value: values.switch)
        sendEvent(name: "hue", value: values.hue)//, isStateChange: true) 
        sendEvent(name: "saturation", value: values.saturation, isStateChange: true) 
    }
}

def setHue(inHue) {
	log.debug "Hue B Smart Group: setHue( ${inHue} )."
    def sat = this.device.currentValue("saturation") ?: 100
	sendToHub([saturation:sat, hue:inHue])
}

def setSaturation(inSat) {
	log.debug "Hue B Smart Group: setSaturation( ${inSat} )."
    
	def hue = this.device.currentValue("hue") ?: 70
	sendToHub([saturation:inSat, hue:hue])
}

def setHueUsing100(inHue) {
	log.debug "Hue B Smart Bulb: setHueUsing100( ${inHue} )."
    
	if (inHue > 100) { inHue = 100 }
    if (inHue < 0) { inHue = 0 }
	def sat = this.device.currentValue("saturation") ?: 100

	sendToHub([saturation:sat, hue:inHue])
}

/**
 * capability.colorTemperature 
 **/
def setColorTemperature(inCT) {
	log.trace "Hue B Smart Group: setColorTemperature ( ${inCT} ): "
    
    def colorTemp = inCT ?: this.device.currentValue("colorTemperature")
    colorTemp = Math.round(1000000/colorTemp)
    
	def commandData = parent.getCommandData(device.deviceNetworkId)
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [ct: colorTemp, transitiontime: tt]	//on:true, 
		])
	)
    sendEvent(name: "colormode", value: "CT", isStateChange: true) 
}

/** 
 * capability.switch
 **/
def on() {
	log.trace "Hue B Smart Group: on(): "

    def commandData = parent.getCommandData(device.deviceNetworkId)
	def tt = device.currentValue("transitionTime") as Integer ?: 0
    def percent = device.currentValue("level") as Integer ?: 100
    def level = parent.scaleLevel(percent, true, 254)
    
        return new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: true, bri: level, transitiontime: tt]
		])
     
    //    sendEvent(name: "switch", value: on, isStateChange: true, displayed: true)

}

def off() {
	log.trace "Hue B Smart Group: off(): "
    
    def commandData = parent.getCommandData(device.deviceNetworkId)
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    //parent.sendHubCommand(
    return new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on: false, transitiontime: tt]
		])
//	)
     //   sendEvent(name: "switch", value: off, isStateChange: true, displayed: true)

}

/** 
 * capability.polling
 **/
def poll() {
	log.trace "Hue B Smart Group: poll(): "
	refresh()
}

/**
 * capability.refresh
 **/
def refresh() {
	log.trace "Hue B Smart Group: refresh(): "
	parent.doDeviceSync()
    configure()
}

def reset() {
	log.trace "Hue B Smart Group: reset(): "

    def value = [level:100, saturation:56, hue:23]
    sendToHub(value)
}

/**
 * capability.alert (flash)
 **/

def flash() {
	log.trace "Hue B Smart Group: flash(): "
    def commandData = parent.getCommandData(device.deviceNetworkId)
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [alert: "lselect"]
		])
	)
    
    runIn(5, flash_off)
}

def flash_off() {
	log.trace "Hue B Smart Group: flash_off(): "
    
    def commandData = parent.getCommandData(device.deviceNetworkId)
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [alert: "none"]
		])
	)
}

                
/**
 * Update Status
 **/
private updateStatus(action, param, val) {
	log.trace "Hue B Smart Group: updateStatus ( ${param}:${val} )"
	if (action == "action") {
		switch(param) {
        	case "on":
            	def onoff
            	if (val == true) {
                	sendEvent(name: "switch", value: on, displayed:true, isStateChange: true)                	     
                
                } else {
	            	sendEvent(name: "switch", value: off)
                	sendEvent(name: "effect", value: "none", displayed:true, isStateChange: true)    
                }    
                break
            case "bri":
            	sendEvent(name: "level", value: parent.scaleLevel(val), displayed:true, isStateChange:true) //parent.scaleLevel(val, true, 255))
//                parent.updateGroupBulbs(this.device.currentValue("lights"), "bri", val)
                break
			case "hue":
            	sendEvent(name: "hue", value: parent.scaleLevel(val, false, 65535), displayed:true, isStateChange:true) // parent.scaleLevel(val))
  //              parent.updateGroupBulbs(this.device.currentValue("lights"), "bri", val)                
                break
            case "sat":
            	sendEvent(name: "saturation", value: parent.scaleLevel(val), displayed:true, isStateChange:true) //parent.scaleLevel(val))
    //            parent.updateGroupBulbs(this.device.currentValue("lights"), "bri", val)
                break
			case "ct": 
            	sendEvent(name: "colorTemperature", value: Math.round(1000000/val), displayed:true, isStateChange:true)  //Math.round(1000000/val))
                break
            case "xy": 
            	
                break    
            case "colormode":
            	sendEvent(name: "colormode", displayed:true, value: val, isStateChange: true)
                break
            case "transitiontime":
            	sendEvent(name: "transitionTime", displayed:true, value: val, isStateChange: true)
                break                
            case "effect":
            	sendEvent(name: "effect", value: val, displayed:true, isStateChange: true)
                break
			case "lights":
            	sendEvent(name: "lights", value: val, displayed:true, isStateChange: true)
                break
            case "scene":
            	log.trace "received scene ${val}"
                break    
			default: 
				log.debug("Unhandled parameter: ${param}. Value: ${val}")    
        }
    }
}

void setAdjustedColor(value) {
	log.trace "setAdjustedColor(${value}) ."
	if (value) {

        def adjusted = [:]
        adjusted = value 
    
        value.level = this.device.currentValue("level") ?: 100
        if (value.level > 100) value.level = 100 // null
        log.debug "adjusted = ${adjusted}"
        setColor(value)
    } else {
		log.warn "Invalid color input"
	}
}

def getDeviceType() { return "groups" }

/**
 * capability.colorLoop
 **/
void colorloopOn() {
    log.debug "Executing 'colorloopOn'"
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    def dState = device.latestValue("switch") as String ?: "off"
	def level = device.currentValue("level") ?: 100
    if (level == 0) { percent = 100}

    sendEvent(name: "effect", value: "colorloop", isStateChange: true)
    
	def commandData = parent.getCommandData(device.deviceNetworkId)
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on:true, effect: "colorloop", transitiontime: tt]
		])
	)
}

void colorloopOff() {
    log.debug "Executing 'colorloopOff'"
    def tt = device.currentValue("transitionTime") as Integer ?: 0
    
    def commandData = parent.getCommandData(device.deviceNetworkId)
    sendEvent(name: "effect", value: "none", isStateChange: true)    
	parent.sendHubCommand(new physicalgraph.device.HubAction(
    	[
        	method: "PUT",
			path: "/api/${commandData.username}/groups/${commandData.deviceId}/action",
	        headers: [
	        	host: "${commandData.ip}"
			],
	        body: [on:true, effect: "none", transitiontime: tt]
		])
	)
}


/**
 * Misc
 **/
private getHextoXY(String colorStr) {
    // For the hue bulb the corners of the triangle are:
    // -Red: 0.675, 0.322
    // -Green: 0.4091, 0.518
    // -Blue: 0.167, 0.04

    def cred = Integer.valueOf( colorStr.substring( 1, 3 ), 16 )
    def cgreen = Integer.valueOf( colorStr.substring( 3, 5 ), 16 )
    def cblue = Integer.valueOf( colorStr.substring( 5, 7 ), 16 )

    double[] normalizedToOne = new double[3];
    normalizedToOne[0] = (cred / 255);
    normalizedToOne[1] = (cgreen / 255);
    normalizedToOne[2] = (cblue / 255);
    float red, green, blue;

    // Make red more vivid
    if (normalizedToOne[0] > 0.04045) {
       red = (float) Math.pow(
                (normalizedToOne[0] + 0.055) / (1.0 + 0.055), 2.4);
    } else {
        red = (float) (normalizedToOne[0] / 12.92);
    }

    // Make green more vivid
    if (normalizedToOne[1] > 0.04045) {
        green = (float) Math.pow((normalizedToOne[1] + 0.055) / (1.0 + 0.055), 2.4);
    } else {
        green = (float) (normalizedToOne[1] / 12.92);
    }

    // Make blue more vivid
    if (normalizedToOne[2] > 0.04045) {
        blue = (float) Math.pow((normalizedToOne[2] + 0.055) / (1.0 + 0.055), 2.4);
    } else {
        blue = (float) (normalizedToOne[2] / 12.92);
    }

    float X = (float) (red * 0.649926 + green * 0.103455 + blue * 0.197109);
    float Y = (float) (red * 0.234327 + green * 0.743075 + blue * 0.022598);
    float Z = (float) (red * 0.0000000 + green * 0.053077 + blue * 1.035763);

    float x = (X != 0 ? X / (X + Y + Z) : 0);
    float y = (Y != 0 ? Y / (X + Y + Z) : 0);

    double[] xy = new double[2];
    xy[0] = x;
    xy[1] = y;
    return xy;
}