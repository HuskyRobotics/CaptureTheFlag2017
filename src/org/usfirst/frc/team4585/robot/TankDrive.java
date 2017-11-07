package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.RobotDrive;

public class TankDrive {

	private int motorLeft;
	private int motorRight;
	private RobotDrive chassis;

	private double coeffX = .75;
	private double coeffY = .75;
	private double scaleX = 1;
	private double scaleY = 2;
	private double deadX = .10;
	private double deadY = .10;

	public TankDrive(int leftPort, int rightPort) {
		motorLeft = leftPort;
		motorRight = rightPort;
		chassis = new RobotDrive(motorLeft, motorRight);
		chassis.setExpiration(.1);
	}

	public void arcadeDrive(double magX, double magY) { // x is rotation, y is forward/back
		magX = applyDeadzone(magX, deadX);
		magY = applyDeadzone(magY, deadY);
//
//		// magX = applyCoeff(magX, coeffX);
//		// magY = applyCoeff(magY, coeffY);
//
		magX = applyScale(magX, scaleX);
		magY = applyScale(magY, scaleY);
 
		chassis.arcadeDrive(magY, magX);
	}

	private double applyDeadzone(double input, double deadzone) {
		if (Math.abs(input) >= deadzone) {
			return input;
		} else {
			return 0;
		}
	}

	private double applyCoeff(double input, double coeff) {
		return input * coeff;
	}

	private double applyScale(double input, double power) {
		return Math.copySign(Math.pow(input, power), input);

	}

	public void setDeadzoneX(double deadzone) {
		this.deadX = deadzone;
	}

	public void setDeadzoneY(double deadzone) {
		this.deadY = deadzone;
	}

	public void setCoefficientX(double coeff) {
		this.coeffX = coeff;
	}

	public void setCoefficientY(double coeff) {
		this.coeffY = coeff;
	}

	public void setScaleX(double scale) {
		this.scaleX = scale;
	}

	public void setScaleY(double scale) {
		this.scaleY = scale;
	}
}
