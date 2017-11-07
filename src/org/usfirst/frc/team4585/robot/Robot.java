package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {
	
	int driveLPort = 8;
	int driveRPort = 9;
	int cannonTriggerPort = 0;
	int climbPort = 2;
	
	int joystickPort = 0;
	
	int climbButton = 2;
	int climbInvertButton = 11;
	int fireButton = 1;
	int fireSafetyButton = 3;
	
	long time;
	long startTime;
	long timeTaken;
	int millisPerIteration = 5;
	
	TankDrive chassis = new TankDrive(driveLPort, driveRPort);
	Extreme3DPro joy = new Extreme3DPro(joystickPort);
	//QDSKeyboard keyboard = new QDSKeyboard(joystickPort);
	Launcher gun = new Launcher(cannonTriggerPort);
	Climber climber = new Climber(climbPort);

	public Robot() {
		
	}

	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomous() {
	
	}

	@Override
	public void operatorControl() {
		time = System.currentTimeMillis();
		while(isEnabled() & isOperatorControl()) {
			if (System.currentTimeMillis() >= time + millisPerIteration) {
				startTime=System.currentTimeMillis();
				
				chassis.arcadeDrive(-joy.getZ(), joy.getY());
				//chassis.arcadeDrive(-keyboard.getAxisAD(), keyboard.getAxisWS());
				
				gun.setFiring(joy.getButton(fireButton)&&joy.getButton(fireSafetyButton));
				
				climber.setInverted(joy.getButton(climbInvertButton));
				climber.setSpeed(joy.getThrottle());
				climber.setClimbing(joy.getButton(climbButton));
				
				time = System.currentTimeMillis();
				
				timeTaken = time-startTime;
			}
		}
	}

	@Override
	public void test() {
		
	}
}




