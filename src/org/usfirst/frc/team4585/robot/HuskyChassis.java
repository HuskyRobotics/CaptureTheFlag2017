package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import org.usfirst.frc.team4585.robot.CompassHeading;



public class HuskyChassis extends RobotDrive{
	static final private int MOTOR_LEFT_PORT = 8;
	static final private int MOTOR_RIGHT_PORT = 9;
	
	static final private int LEFT_ENCODER_PORT_A = 0;
	static final private int LEFT_ENCODER_PORT_B = 1;
	static final private int RIGHT_ENCODER_PORT_A = 8;
	static final private int RIGHT_ENCODER_PORT_B = 9;
	
	
	static final private double MAG_DEAD_RANGE = 0.05;
	static final private double ROT_DEAD_RANGE = 0.05;
	
	static final private double INCHES_PER_ENCODER_PULSE = -0.089;
	
	private Joystick _Joy = null;

	private  ADXRS450_Gyro _gyro = new ADXRS450_Gyro();
	
	private final Encoder _LeftEncoder = new Encoder(LEFT_ENCODER_PORT_A, LEFT_ENCODER_PORT_B);
	private final Encoder _RightEncoder = new Encoder(RIGHT_ENCODER_PORT_A, RIGHT_ENCODER_PORT_B);

	
	//=================================================

	private class HuskySpeedControl{
		static private final double SPEED_CONTROL_MAX = 1.00;
		static private final double SPEED_CONTROL_MIN = 0.50;
		static private final double ACCELERATION_VAL = 0.001;
		
		private double _TargSpeed = 0.0;
		private double _CurSpeed = 0.0;
		
		
		public HuskySpeedControl(){
		}
		
		
		public void SetTargSpeed(double TargSpeed){
			_TargSpeed = TargSpeed;

			if(_TargSpeed > 0){
				if(_TargSpeed > SPEED_CONTROL_MAX){
					_TargSpeed = SPEED_CONTROL_MAX;
				}
				else{
					if(_TargSpeed < SPEED_CONTROL_MIN){
						_TargSpeed = 0.0;
					}
				}
			}
			else{
				if(Math.abs(_TargSpeed) > SPEED_CONTROL_MAX){
					_TargSpeed = -SPEED_CONTROL_MAX;
				}
				else{
					if(Math.abs(_TargSpeed) < SPEED_CONTROL_MIN){
						_TargSpeed = 0.0;
					}
				}
			}
		}

		
		public void AdjustSpeed(){
			if(_TargSpeed > 0){
				if(_TargSpeed != _CurSpeed){
					if(_TargSpeed < _CurSpeed){
						// slow down
						_CurSpeed -= ACCELERATION_VAL;
						
						if(_CurSpeed < SPEED_CONTROL_MIN){
							_CurSpeed = 0.0;
						}
					}
					else{
						// speed up
						if(_CurSpeed < SPEED_CONTROL_MIN){
							_CurSpeed = SPEED_CONTROL_MIN;
						}
						else{
							_CurSpeed += ACCELERATION_VAL;
							if(_CurSpeed > SPEED_CONTROL_MAX){
								_CurSpeed = SPEED_CONTROL_MAX;
							}
						}
					}
				}
			}
			else{
				if(_TargSpeed != _CurSpeed){
					if(_TargSpeed > _CurSpeed){
						// slow down
						_CurSpeed += ACCELERATION_VAL;
						
						if(Math.abs(_CurSpeed) < SPEED_CONTROL_MIN){
							_CurSpeed = 0.0;
						}
					}
					else{
						// speed up
						if(Math.abs(_CurSpeed) < SPEED_CONTROL_MIN){
							_CurSpeed = -SPEED_CONTROL_MIN;
						}
						else{
							_CurSpeed -= ACCELERATION_VAL;
							if(Math.abs(_CurSpeed) > SPEED_CONTROL_MAX){
								_CurSpeed = -SPEED_CONTROL_MAX;
							}
						}
					}
				}
			}
		}
		
		public double GetUseSpeed(){
			return _CurSpeed;
		}
	}
	
	//=================================================
	

	private class HuskyRotationControl{
		
		static final private double CLOCKWISE_ROTATION_SPEED_HI = -0.55;
		static final private double CLOCKWISE_ROTATION_SPEED_LO = -0.3;
		static final private double COUNTERCLOCKWISE_ROTATION_SPEED_HI = 0.55;
		static final private double COUNTERCLOCKWISE_ROTATION_SPEED_LO = 0.3;
		
		private CompassHeading _TargHeading = new CompassHeading();
		private CompassHeading _CurHeading = new CompassHeading();
		
		public HuskyRotationControl(){
			
		}
		
		public void AdjustRotationControl(){
			_CurHeading.SetValue(Math.round(_gyro.getAngle() * 10.0) / 10.0);
		}
		
		
		public void SetTargHeading(double NewHeading){
			_TargHeading.SetValue(NewHeading);
		}
		
		public double GetCurrentHeading(){
			return _CurHeading.GetValue();
		}
		
//		public double GetTargHeading(){
//			return _TargHeading.GetValue();
//		}
		
		public double GetUseRotation(){
			double RotationVal = 0.0;
			
			if(!_TargHeading.EssentiallyEquals(_CurHeading)){
				double DiffMag = Math.abs(_TargHeading.AngleDifference(_CurHeading));
				if(_TargHeading.IsClockWiseFrom(_CurHeading)){
					if(DiffMag > 5.0){
						RotationVal = CLOCKWISE_ROTATION_SPEED_HI;
					}
					else{
						RotationVal = CLOCKWISE_ROTATION_SPEED_LO;
					}
				}
				else{
					if(DiffMag > 5.0){
						RotationVal = COUNTERCLOCKWISE_ROTATION_SPEED_HI;
					}
					else{
						RotationVal = COUNTERCLOCKWISE_ROTATION_SPEED_LO;
					}
				}
			}
			
			return RotationVal;
		}
	}
	
	//=================================================
	

	private HuskySpeedControl _SpeedFilter = new HuskySpeedControl();
	private HuskyRotationControl _RotationFilter = new HuskyRotationControl();
	
	public HuskyChassis(Joystick Joy){
		super(MOTOR_LEFT_PORT, MOTOR_RIGHT_PORT);
		_Joy = Joy;
		
		_LeftEncoder.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		_RightEncoder.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		
		_gyro.calibrate();
	}
	
	public void OperatorSetup(){
		ResetDistance();
		ResetHeading();
		_SpeedFilter.SetTargSpeed(0.0);
	}
	
	public void AutonomousSetup(){
		ResetDistance();
		ResetHeading();
		_SpeedFilter.SetTargSpeed(0.0);
	}
	
	
	
	private double GetForwardBack(){
		double ForBackMag = -_Joy.getY();
		
		if(Math.abs(ForBackMag) < MAG_DEAD_RANGE){
			ForBackMag = 0.0;		// In the dead range, force to 0
		}
		else{
			
		}
		
		return ForBackMag;
	}
	
	
	private double GetRotation(){
		double DirectionMag = -_Joy.getTwist();
		
		if(Math.abs(DirectionMag) < ROT_DEAD_RANGE){
			DirectionMag = 0.0;		// In the dead range, force to 0
		}
		else{
			
		}

		return DirectionMag;
	}
	
	public void DoAnalogControl(){
		arcadeDrive(GetForwardBack(), GetRotation());
		
		int LeftCount = _LeftEncoder.get();
		boolean LeftDirection = _LeftEncoder.getDirection();
		double LeftRate = _LeftEncoder.getRate();
		int LeftRaw = _LeftEncoder.getRaw();
		boolean LeftStopped = _LeftEncoder.getStopped();
		
		int RightCount = _RightEncoder.get();
		boolean RightDirection = _RightEncoder.getDirection();
		double RightRate = _RightEncoder.getRate();
		int RightRaw = _RightEncoder.getRaw();
		boolean RightStopped = _RightEncoder.getStopped();
		
		SmartDashboard.putNumber("Left Count", LeftCount);
		SmartDashboard.putBoolean("Left Direction", LeftDirection);
		SmartDashboard.putNumber("Left Rate", LeftRate);
		SmartDashboard.putNumber("Left Raw", LeftRaw);
		SmartDashboard.putBoolean("Left Stopped", LeftStopped);
		
		SmartDashboard.putNumber("Right Count", RightCount);
		SmartDashboard.putBoolean("Right Direction", RightDirection);
		SmartDashboard.putNumber("Right Rate", RightRate);
		SmartDashboard.putNumber("Right Raw", RightRaw);
		SmartDashboard.putBoolean("Right Stopped", RightStopped);
		
		double CurrentHeading = GetCurrentHeading();
		double CurrentDistance = GetCurrentDistance();
		SmartDashboard.putNumber("Current Heading", CurrentHeading);
		SmartDashboard.putNumber("Current Distance", CurrentDistance);
		
	}
	
	
	public void DoAutonomousControl(){
		_SpeedFilter.AdjustSpeed();
		_RotationFilter.AdjustRotationControl();
		
		arcadeDrive(_SpeedFilter.GetUseSpeed(), _RotationFilter.GetUseRotation());
//		arcadeDrive(_SpeedFilter.GetUseSpeed(), 0.0);
		SmartDashboard.putNumber("Auto Speed", _SpeedFilter.GetUseSpeed());
		SmartDashboard.putNumber("Auto Steering", _RotationFilter.GetUseRotation());
	}
	
	public void MoveChassis(double DesiredSpeed, double DesiredHeading){
		SmartDashboard.putNumber("Set Speed", DesiredSpeed);

		_SpeedFilter.SetTargSpeed(DesiredSpeed);
		_RotationFilter.SetTargHeading(DesiredHeading);
	}
	
	public void RotateChassis(double RotationSpeed){
		arcadeDrive(0.0, RotationSpeed);
		
	}
	
	
	public void StopChassis(){
		arcadeDrive(0.0, 0.0);
	}
	
	public void ResetDistance(){
		_LeftEncoder.reset();
		_RightEncoder.reset();
	}
	
	public double GetCurrentDistance(){
		return _LeftEncoder.getDistance();
	}
	
	public void ResetHeading(){
		_gyro.reset();
	}

	public double GetCurrentHeading(){
		return _RotationFilter.GetCurrentHeading();
	}
	
}	// HuskyChassis class


