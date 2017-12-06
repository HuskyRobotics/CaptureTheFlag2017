package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.*;
import org.usfirst.frc.team4585.robot.TankDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;

public class Robot extends SampleRobot {
	final int MILLISPERITERATION = 5;
	//DrivePorts
	int driveLPort = 8;
	int driveRPort = 9;
	//Joystick
	int joystickPort = 0;
	//Claw
	int clawPort = 7;
	//PingPongShooter
	
	
	//Initialization of robot functions
	Extreme3DPro joy = new Extreme3DPro(joystickPort);
	TankDrive chassis = new TankDrive(driveLPort, driveRPort, joy);
	PingPongShooter shooter = new PingPongShooter(joy);
	RoboClaw claw = new RoboClaw(clawPort);
	
	GhostController Marcus = new GhostController(chassis, claw);
	AnalogInput JunkAnalog = new AnalogInput(2);
	SmartDashboard JunkDash = new SmartDashboard();
	
	public Robot() {
		
	}

	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomous() {
		final long AUTONMOUS_TIME_MILLI = 600;
		long time = System.currentTimeMillis();
		long ExpireTime = time + AUTONMOUS_TIME_MILLI;

		int CycleCounter = 0;
		Marcus.Reset();
		while(isEnabled() && (time < ExpireTime)){
		
			if(System.currentTimeMillis() >= (time + MILLISPERITERATION)){
				Marcus.AutonomousControl();
				time = System.currentTimeMillis();
			}
		}
		claw.move(false, false);
	}

	
	//Loop checking for robot movement
	@Override
	public void operatorControl() {

		long time;
		time = System.currentTimeMillis();
		while(isEnabled() & isOperatorControl()) {
			if (System.currentTimeMillis() >= time + MILLISPERITERATION) {
				JunkDash.putDouble("Voltage", JunkAnalog.getVoltage());
				
				
				chassis.arcadeDrive();
				shooter.operatorControl();
				claw.move(joy.getButton(5),joy.getButton(3));
				time = System.currentTimeMillis();
				
			}
		}
	}

	@Override
	public void test() {
		
	}
}




