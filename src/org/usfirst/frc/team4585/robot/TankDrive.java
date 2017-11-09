package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.RobotDrive;

public class TankDrive {
	private RobotDrive chassis;

	private final double SCALEX = 1;
	private final double SCALEY = 2;
	private final double DEADX = .10;
	private final double DEADY = .10;

	public TankDrive(int MOTORLEFT, int MOTORRIGHT) {
		chassis = new RobotDrive(MOTORLEFT, MOTORRIGHT);
		chassis.setExpiration(.1);
	}

	public void arcadeDrive(double magX, double magY) { // x is rotation, y is forward/back
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

}
