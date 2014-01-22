package devices.sensors;

public class Infrared extends AnalogInput {
	
	/*
	 * Takes one analog-in pin (labeled on the Maple as "AIN")
	 */
	public Infrared(int pin) {
		super(pin);
	}
	
	public float getDistance() {
		// TODO: conversion
		return (float) value;
	}

}
