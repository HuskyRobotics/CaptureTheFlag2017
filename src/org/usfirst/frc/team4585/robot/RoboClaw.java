package org.usfirst.frc.team4585.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.*;


public class RoboClaw{
	static private final int SPARK_MOTOR_PORT = 7;
	static private final int UP_BUTTON = 5;
	static private final int DOWN_BUTTON = 3;
	static private final int AUTO_MOVE_TIME_DOWN = 500;
	static private final int AUTO_MOVE_TIME_UP = 550;
	
	private Spark _motor = new Spark(SPARK_MOTOR_PORT);
	private double _speed = 0.5;
	private Joystick _Joy = null;
	private boolean _ManualControl = false;
	private boolean _AutoMovingUp = false;
	private boolean _AutoMovingDown = false;
	private boolean _FullUp = true;
	private boolean _FullDown = false;
	private long _AutoMoveExpireTime = 0;
	private double _Voltage = 0.0;
	

	public RoboClaw(Joystick Joy){
		_Joy = Joy;
	}
	
	
	public void OperatorSetup(){
		ShowDebug();
	}
	
	public void AutonomousSetup(){
		ShowDebug();
	}
	
	public void DoControl(){
		_ManualControl = true;
		SmartDashboard.putBoolean("Button 5", _Joy.getRawButton(UP_BUTTON));
		SmartDashboard.putBoolean("Button 3", _Joy.getRawButton(DOWN_BUTTON));

		if(_Joy.getRawButton(UP_BUTTON)){
			ClawMoveUp();
		}
		else{
			if(_Joy.getRawButton(DOWN_BUTTON)){
				ClawMoveDown();
			}
			else{
				ClawStop();
			}
		}
		_ManualControl = false;
	}
	
	public void ClawMoveUp(){
		if(_ManualControl){
			_motor.set(_speed);
		}
		else{
			if(!_AutoMovingUp){
				_AutoMoveExpireTime = System.currentTimeMillis() + AUTO_MOVE_TIME_UP;
				_FullUp = false;
				_FullDown = false;
				_AutoMovingUp = true;
				_AutoMovingDown = false;
			}
			else{
				if(System.currentTimeMillis() >= _AutoMoveExpireTime){
					_FullUp = true;
				}
				else{
					_motor.set(_speed);
				}
			}
		}
		
		ShowDebug();
	}
	
	public void ClawMoveDown(){
		if(_ManualControl){
			_motor.set(-_speed);
		}
		else{
			if(!_AutoMovingDown){
				_AutoMoveExpireTime = System.currentTimeMillis() + AUTO_MOVE_TIME_DOWN;
				_FullUp = false;
				_FullDown = false;
				_AutoMovingUp = false;
				_AutoMovingDown = true;
			}
			else{
				if(System.currentTimeMillis() >= _AutoMoveExpireTime){
					_FullDown = true;
				}
				else{
					_motor.set(-_speed);
				}
			}
		}
		
		ShowDebug();
	}
	
	public void ClawStop(){
		_motor.set(0.0);
		_AutoMovingUp = false;
		_AutoMovingDown = false;
	}
	
	
	public boolean IsFullUp(){
		return _FullUp;
	}
	
	public boolean IsFullDown(){
		return _FullDown;
	}
	
	private void ShowDebug(){
		SmartDashboard.putNumber("Voltage", _Voltage);
		
	}
}



/***********************************

public class RoboClaw {
	Spark motor;
	private double speed = 0.5;
	
	public RoboClaw(int port){
		motor = new Spark(port);
	}
	
	public void move(boolean up, boolean down){
		if(up && !down){
			motor.set(speed);
		}else if(!up && down){
			motor.set(-speed);
		}else{
			motor.set(0);
		}
	}
	
	public void setSpeed(double newSpeed){
		this.speed = newSpeed;
	}
}


*****************************/