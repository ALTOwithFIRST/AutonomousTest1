package org.usfirst.frc.team1523.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.awt.Color;

import com.ctre.CANTalon;


/**
 * This sample program shows how to control a motor using a joystick. In the
 * operator control part of the program, the joystick is read and the value is
 * written to the motor.
 *
 * Joystick analog values range from -1 to 1 and speed controller inputs also
 * range from -1 to 1 making it easy to work together. The program also delays a
 * short time in the loop to allow other threads to run. This is generally a
 * good idea, especially since the joystick values are only transmitted from the
 * Driver Station once every 20ms.
 */
public class Robot extends SampleRobot {

	private Joystick stick = new Joystick(0); // initialize the joystick on port
												// 0
	NetworkTable Ntable;

	private final double kUpdatePeriod = 0.005; // update every 0.005 seconds/5
    												// milliseconds (200Hz)
	CANTalon motorL = new CANTalon(12);
	CANTalon motorR = new CANTalon(13);
	RobotDrive myrobot = new RobotDrive(motorL, motorR );
	double moveValue = 0;
	double rotateValue = 0;
	double lastMoveValue = 0;
	double lastRotateValue = 0;
	int button;
	// Set these constants to fine-tune Autonomous motion
	public static final double MAX_MOVVAL = 0.4;  
	public static final double MAX_ROTVAL = 0.3;  
	public static final double MOVGAIN = 0.01;  
	public static final double ROTGAIN = 0.003;  
	public static final int SET_TARGET_BUTTON = 1;  
	
	public Robot() {
		motorL.setInverted(true);
		motorR.setInverted(true);
		UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture();
		camera1.setResolution(640,480);
		camera1.setExposureManual(10);
		MjpegServer mjpegserver1 = new MjpegServer("serve_USB Camera 0",1181);
		// put this as IP camera address in GRID
		// http://roborio-1523-frc.local:1181?action=stream
		Ntable = NetworkTable.getTable("GRIP/myContoursReport");
		
	}

	/**
	 * Runs the motor from a joystick.
	 */
	@Override
	public void operatorControl() {
		while (isOperatorControl() && isEnabled()) {
			// Set the motor's output.
			// This takes a number from -1 (100% speed in reverse) to +1 (100%
			// speed going forward)
//			motor.set(stick.getY());
			double[] defaultValue = new double[0];
			double[] areas = Ntable.getNumberArray("area", defaultValue);
			double[] centerX = Ntable.getNumberArray("centerX", defaultValue);
			double[] width = Ntable.getNumberArray("width", defaultValue);

			SmartDashboard.putNumber("NetworkTable Length", width.length);
			
			if (width.length > 0) {

			SmartDashboard.putNumber("areas", areas[0]);
			SmartDashboard.putNumber("centerX", centerX[0]);
			SmartDashboard.putNumber("width", width[0]);
			}
			if (width.length > 2) {
				SmartDashboard.putString("Image Integrity", "** NOISY IMAGE DATA **");			
			}
			else if (width.length == 0){
				SmartDashboard.putString("Image Integrity", "** NO TARGET IMAGE DATA **");			
			}
			else {
				SmartDashboard.putString("Image Integrity", "** TARGET IMAGE DATA GOOD **");	
			}

			if (true == stick.getRawButton(SET_TARGET_BUTTON)) {
				SmartDashboard.putNumber("TargetCenterX", centerX[0]);
				SmartDashboard.putNumber("TargetWidth", width[0]);
			}
			double TargetCenterX = SmartDashboard.getNumber("TargetCenterX", 0.0);
			double TargetWidth = SmartDashboard.getNumber("TargetWidth", 0.0);
			if ((TargetCenterX == 0.0) || (TargetWidth == 0.0)) {
				SmartDashboard.putString("Target Status: ", "** Warning, must load or set Target Data **");			
			}
			else {
				SmartDashboard.putString("Target Status: ", "** Target Data Loaded **");			
			}


			moveValue = stick.getY();
			rotateValue = stick.getX();

			if (moveValue > MAX_MOVVAL) {
				moveValue = MAX_MOVVAL;
			}
			if (moveValue < -MAX_MOVVAL) {
				moveValue = -MAX_MOVVAL;
			}
			if (rotateValue > MAX_ROTVAL) {
				rotateValue = MAX_ROTVAL;
			}
			if (rotateValue < -MAX_ROTVAL) {
				rotateValue = -MAX_ROTVAL;
			}

			SmartDashboard.putNumber("moveValue", moveValue);
			SmartDashboard.putNumber("rotateValue", rotateValue);

			
			myrobot.arcadeDrive(moveValue,rotateValue,true);
//			myrobot.arcadeDrive(stick, true);

			Timer.delay(kUpdatePeriod); // wait 5ms to the next update
		}
	}

	@Override
	public void autonomous() {
		while (isAutonomous() && isEnabled()) {
			double[] defaultValue = new double[0];
			double[] areas = Ntable.getNumberArray("area", defaultValue);
			double[] centerX = Ntable.getNumberArray("centerX", defaultValue);
			double[] width = Ntable.getNumberArray("width", defaultValue);

			SmartDashboard.putNumber("NetworkTable Length", width.length);
			
			if (width.length > 0) {

			SmartDashboard.putNumber("areas", areas[0]);
			SmartDashboard.putNumber("centerX", centerX[0]);
			SmartDashboard.putNumber("width", width[0]);
			}
			if (width.length > 2) {
				SmartDashboard.putString("Image Integrity", "** NOISY IMAGE DATA **");			
			}
			else if (width.length == 0){
				SmartDashboard.putString("Image Integrity", "** NO TARGET IMAGE DATA **");			
			}
			else {
				SmartDashboard.putString("Image Integrity", "** TARGET IMAGE DATA GOOD **");	
			}

			if (true == stick.getRawButton(SET_TARGET_BUTTON)) {
				SmartDashboard.putNumber("TargetCenterX", centerX[0]);
				SmartDashboard.putNumber("TargetWidth", width[0]);
			}
			double TargetCenterX = SmartDashboard.getNumber("TargetCenterX", 0.0);
			double TargetWidth = SmartDashboard.getNumber("TargetWidth", 0.0);
			if ((TargetCenterX == 0.0) || (TargetWidth == 0.0)) {
				SmartDashboard.putString("Target Status: ", "** Warning, must load or set Target Data **");			
			}
			else {
				SmartDashboard.putString("Target Status: ", "** Target Data Loaded **");			
			}

			// Calculate the move and rotate values based on the image compared to the target image
			lastMoveValue = moveValue;
			lastRotateValue = rotateValue;
			moveValue = -(TargetWidth - width[0])*MOVGAIN;
			rotateValue = -(TargetCenterX - centerX[0])*ROTGAIN;
			moveValue = (2*lastMoveValue + moveValue)/3;
			rotateValue = (2*lastRotateValue + rotateValue)/3;

//			moveValue = stick.getY();
//			rotateValue = stick.getX();

			if (moveValue > 0) {  // don't go backwards
				moveValue = 0;
			}
			if (moveValue < -MAX_MOVVAL) {
				moveValue = -MAX_MOVVAL;
			}
			if (rotateValue > MAX_ROTVAL) {
				rotateValue = MAX_ROTVAL;
			}
			if (rotateValue < -MAX_ROTVAL) {
				rotateValue = -MAX_ROTVAL;
			}

			SmartDashboard.putNumber("moveValue", moveValue);
			SmartDashboard.putNumber("rotateValue", rotateValue);

			
			myrobot.arcadeDrive(moveValue,rotateValue,true);
//			myrobot.arcadeDrive(stick, true);

			Timer.delay(kUpdatePeriod); // wait 5ms to the next update
		}
	}
}
