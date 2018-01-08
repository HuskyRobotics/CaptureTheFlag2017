package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.*;
import org.usfirst.frc.team4585.robot.TankDrive;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;


public class Robot extends SampleRobot {
	final int JOYSTICK_PORT = 0;
	final long ROBOT_ACTION_LOOPTIME = 5;		// 5 milliseconds
//	final long AUTONMOUS_TIME = 15000;			// 15 seconds
	final long AUTONMOUS_TIME = 60000;			// 15 seconds
	
	
	private Joystick _joy = new Joystick(JOYSTICK_PORT);
	private RoboClaw _theClaw = new RoboClaw(_joy);
	private HuskyChassis _theChassis = new HuskyChassis(_joy);
	private GhostController _theGhost = new GhostController(_theChassis, _theClaw);
	
	
	public Robot() {
		
	}

	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomous() {
		_theGhost.SetupForAutonomous();
		
		long time = System.currentTimeMillis();
		long ExpireTime = time + AUTONMOUS_TIME;
		long TargTime = time;

		while(isEnabled() && (time < ExpireTime)){
			
			if(time >= TargTime){
				_theGhost.DoAutonomousControl();
				_theChassis.DoAutonomousControl();
				TargTime += ROBOT_ACTION_LOOPTIME;
			}
			
			time = System.currentTimeMillis();
		}
	}

	
	//Loop checking for robot movement
	@Override
	public void operatorControl() {
		long targTime = System.currentTimeMillis() + ROBOT_ACTION_LOOPTIME;
		_theClaw.OperatorSetup();
		_theChassis.OperatorSetup();
				
		while(isEnabled() & isOperatorControl()) {
			if (System.currentTimeMillis() >= targTime) {
				
				_theClaw.DoControl();
				_theChassis.DoAnalogControl();
				
				targTime += ROBOT_ACTION_LOOPTIME;
			}
		}
	}

	@Override
	public void test() {
		
	}
}





/******************

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
	int loadShoot;
	int leftShoot;
	int rightShoot;
	
	//Initialization of robot functions
	Extreme3DPro joy = new Extreme3DPro(joystickPort);
	TankDrive chassis = new TankDrive(driveLPort, driveRPort, joy);
	//PingPongShooter shooter = new PingPongShooter(loadShoot, leftShoot, rightShoot);
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

		
		final long AUTONMOUS_TIME_MILLI = 15000;
		long time = System.currentTimeMillis();
		long ExpireTime = time + AUTONMOUS_TIME_MILLI;

		int CycleCounter = 0;
		Marcus.Reset();
		//Marcus.AutonomousControl();
		while(isEnabled() && (time < ExpireTime)){
			
			if(System.currentTimeMillis() >= (time + MILLISPERITERATION)){
				Marcus.AutonomousControl();
				time = System.currentTimeMillis();
			}
		}
//		claw.move(false, false);
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
				//shooter.operatorControl();
				//shooter.loadBall(joy.getButton(2));
				//shooter.Shoot(joy.getTrigger());
				claw.move(joy.getButton(5),joy.getButton(3));
				time = System.currentTimeMillis();
				
			}
		}
	}

	@Override
	public void test() {
		
	}
}

******************/


