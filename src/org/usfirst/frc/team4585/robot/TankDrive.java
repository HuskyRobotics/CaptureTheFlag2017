package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TankDrive {
	private RobotDrive chassis;
	private Extreme3DPro _joy;
	private AnalogGyro _gyro;
	SmartDashboard dash = new SmartDashboard();
	
	private final double ROTATIONCONSTANT = 1;
	private final double SCALEX = 1;
	private final double SCALEY = 2;
	private final double DEADX = .5;
	private final double DEADY = .10;

	public TankDrive(int MOTORLEFT, int MOTORRIGHT, Extreme3DPro joyparam) {
		chassis = new RobotDrive(MOTORLEFT, MOTORRIGHT);
		chassis.setExpiration(.1);
		_joy = joyparam;
		_gyro = new AnalogGyro(1);
	}

	public void arcadeDrive() { // x is rotation, y is forward/back
		double magX = -_joy.getZ();
		double magY = _joy.getY();
		
		magX = applyDeadzone(magX, DEADX);
		magY = applyDeadzone(magY, DEADY);

		magX = applyScale(magX, SCALEX);
		magY = applyScale(magY, SCALEY);
 
		chassis.arcadeDrive(magY, magX);
	}

	private double applyDeadzone(double input, double deadzone) {
		if (Math.abs(input) >= deadzone) {
			return input;
		} else {
			return 0;
		}
	}


	private double applyScale(double input, double power) {
		return Math.copySign(Math.pow(input, power), input);

	}
	
	public void Spin4While(){
		chassis.arcadeDrive(0, 1);
	}
	
	public void MoveSlowly(){
		chassis.arcadeDrive(0.5, ROTATIONCONSTANT * _gyro.getAngle());
		dash.putDouble("GyroAngle", _gyro.getAngle());
	}
	
	public void MoveBackSlowly(){
		chassis.arcadeDrive(-0.5, ROTATIONCONSTANT * _gyro.getAngle());
	}
	
	public void Reset(){
		_gyro.reset();
	}
	
	public void doNothing(){
		dash.putDouble("GyroAngle", _gyro.getAngle());
	}

}
