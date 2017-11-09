package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import org.usfirst.frc.team4585.robot.TankDrive;

public class Robot extends SampleRobot {
	//DrivePorts
	int driveLPort = 8;
	int driveRPort = 9;
	//Joystic
	int joystickPort = 0;
	//Claw
	
	//PingPongShooter
	
	
	//Initilization of robot functions
	TankDrive chassis = new TankDrive(driveLPort, driveRPort);
	Extreme3DPro joy = new Extreme3DPro(joystickPort);

	public Robot() {
		
	}

	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomous() {
	
	}

	
	//Loop checking for robot movement
	@Override
	public void operatorControl() {
		long time;
		final int MILLISPERITERATION = 5;
		time = System.currentTimeMillis();
		while(isEnabled() & isOperatorControl()) {
			if (System.currentTimeMillis() >= time + MILLISPERITERATION) {
				
				chassis.arcadeDrive(-joy.getZ(), joy.getY());
				
				time = System.currentTimeMillis();
				
			}
		}
	}

	@Override
	public void test() {
		
	}
}




