package org.usfirst.frc.team4585.robot;
import edu.wpi.first.wpilibj.Spark;

public class PingPongShooter {
	private double loadMotorSpeed;
	private double shootMotorSpeed;
	Spark loadMotor;
	Spark motorLeft;
	Spark motorRight;
	
	
	public PingPongShooter(int loadPort, int leftPort, int rightPort)
	{
		loadMotor = new Spark(loadPort);
		motorLeft = new Spark(leftPort);
		motorRight = new Spark(rightPort);
	}
	
	
	public void loadBall(boolean loadButtonPushed)
	{
		if(loadButtonPushed ){
			loadMotor.set(loadMotorSpeed);
		}
	}
	
	public void Shoot(boolean shootButtonPushed)
	{
		if(shootButtonPushed){
				motorLeft.set(-shootMotorSpeed);
				motorRight.set(shootMotorSpeed);
		}
		
	}
	
	public void setLoadSpeed(int launchSpeed){
		this.loadMotorSpeed = launchSpeed;
	}
	
	public void setShootSpeed(){
		
	}
	
	public void operatorControl()
	{
		
	}
	
}
