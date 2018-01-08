package org.usfirst.frc.team4585.robot;

//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.*;
import org.usfirst.frc.team4585.robot.CompassHeading;


class GhostController{
	
	private static enum TaskType {
		DoNothing,
		Pause,
		MoveAhead,
		MoveBack,
		Rotate,
		RaiseClaw,
		LowerClaw,
	}
	
	//=================================================
	
	static private class TaskElement{
		private TaskType _Type;
		private double _ParamValue;
		
		public TaskElement(TaskType InType, double InParam){
			_Type = InType;
			_ParamValue = InParam;
		}
		
		public TaskType getTaskType(){
			return _Type;
		}
		
		public double getParamValue(){
			return _ParamValue;
		}
	}
	
	//=================================================
	
	static private ArrayList<TaskElement> sBackForthInst = new ArrayList<TaskElement>();
	static private ArrayList<TaskElement> sRotateTestInst = new ArrayList<TaskElement>();
	static private ArrayList<TaskElement> sClawTestInst = new ArrayList<TaskElement>();
	static private ArrayList<TaskElement> sMixTestInst = new ArrayList<TaskElement>();
	static private ArrayList<TaskElement> sMixTestInst2 = new ArrayList<TaskElement>();
	
	static {
		sBackForthInst.add(new TaskElement(TaskType.MoveAhead, 300));
//		sBackForthInst.add(new TaskElement(TaskType.MoveBack, 200));
		
		sRotateTestInst.add(new TaskElement(TaskType.Rotate, 120));
		sRotateTestInst.add(new TaskElement(TaskType.Pause, 1000));
		sRotateTestInst.add(new TaskElement(TaskType.Rotate, -90));
		sRotateTestInst.add(new TaskElement(TaskType.Pause, 1000));
		sRotateTestInst.add(new TaskElement(TaskType.Rotate, 180));
		
		sClawTestInst.add(new TaskElement(TaskType.LowerClaw, 0));
		sClawTestInst.add(new TaskElement(TaskType.Pause, 1000));
		sClawTestInst.add(new TaskElement(TaskType.RaiseClaw, 0));

		sMixTestInst.add(new TaskElement(TaskType.Rotate, -90));
		sMixTestInst.add(new TaskElement(TaskType.MoveAhead, 40));
		sMixTestInst.add(new TaskElement(TaskType.LowerClaw, 0));
		sMixTestInst.add(new TaskElement(TaskType.Rotate, 90));
		sMixTestInst.add(new TaskElement(TaskType.MoveAhead, 10));
		sMixTestInst.add(new TaskElement(TaskType.RaiseClaw, 0));
		sMixTestInst.add(new TaskElement(TaskType.MoveBack, 10));
		sMixTestInst.add(new TaskElement(TaskType.Rotate, 180));
		sMixTestInst.add(new TaskElement(TaskType.MoveAhead, 20));

		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 50));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, -90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 250));
		sMixTestInst.add(new TaskElement(TaskType.LowerClaw, 0));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, 90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 40));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, 90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 250));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, 90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 40));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, 90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 40));
		sMixTestInst2.add(new TaskElement(TaskType.MoveBack, 10));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, -90));
		sMixTestInst2.add(new TaskElement(TaskType.MoveAhead, 50));
		sMixTestInst2.add(new TaskElement(TaskType.Rotate, 180));
	}
	
	//=================================================
	//=================================================

	
	private HuskyChassis _Chassis = null;
	private RoboClaw _Claw = null;
	
	private TaskType _CurTask = TaskType.DoNothing;
	
	private long _PauseExpire = 0;
	
	private double _TargDistance = 0.0;
	private double _CurrentDistance = 0.0;

	private CompassHeading _TargHeading = new CompassHeading();
	private CompassHeading _CurHeading = new CompassHeading();
	
	private ListIterator<TaskElement> _InstructIter;
//	private GhostSpeedControl _SpeedController = new GhostSpeedControl();
	
	public GhostController(HuskyChassis Chassis, RoboClaw Claw){
		_Chassis = Chassis;
		_Claw = Claw;
	}
	
	public void SetupForAutonomous(){
		_Chassis.AutonomousSetup();
		_Claw.AutonomousSetup();
		
//		_InstructIter = sBackForthInst.listIterator();
//		_InstructIter = sRotateTestInst.listIterator();
//		_InstructIter = sClawTestInst.listIterator();
//		_InstructIter = sMixTestInst.listIterator();
		_InstructIter = sMixTestInst2.listIterator();
		
		SetNextTask();
	}

	public void DoAutonomousControl(){
		
		_CurHeading.SetValue(_Chassis.GetCurrentHeading());
		_CurrentDistance = _Chassis.GetCurrentDistance();
		
		switch(_CurTask){
		
			case MoveAhead:
				DoTaskMoveAhead();
				break;
				
			case MoveBack:
				DoTaskMoveBack();
				break;
			
			case Rotate:
				DoTaskRotate();
				break;
			
			case RaiseClaw:
				DoTaskRaiseClaw();
				break;
			
			case LowerClaw:
				DoTaskLowerClaw();
				break;
			
			case Pause:
				DoTaskPause();
				break;
				
			case DoNothing:
				break;
				
			default:
				// error condition. shouldn't be here
				break;
		
		}
		
		SmartDashboard.putNumber("Auto Distance", _CurrentDistance);
		SmartDashboard.putNumber("Auto Heading", _Chassis.GetCurrentHeading());
		SmartDashboard.putNumber("Target Distance", _TargDistance);
		SmartDashboard.putNumber("Target Heading", _TargHeading.GetValue());
	}
	
	
	private void SetNextTask(){
		if(_InstructIter.hasNext()){
			TaskElement CurTaskElement = _InstructIter.next();
			
			switch(CurTaskElement.getTaskType()){
				case MoveAhead:
					SetTaskMoveAhead(CurTaskElement.getParamValue());
					break;
			
				case MoveBack:
					SetTaskMoveBack(CurTaskElement.getParamValue());
					break;
					
				case Rotate:
					SetTaskRotate(CurTaskElement.getParamValue());
					break;
					
				case RaiseClaw:
					SetTaskRaiseClaw();
					break;
					
				case LowerClaw:
					SetTaskLowerClaw();
					break;
					
				case Pause:
					SetTaskPause(CurTaskElement.getParamValue());
					break;
				
				case DoNothing:
					SetTaskDoNothing();
					break;
				
				default:
					// error: shouldn't be here
					break;
			}
		}
		else{
			SetTaskDoNothing();
		}
	}
	
	private void TaskCompleted(){
		SetNextTask();
	}
	
	private void SetTaskMoveAhead(double TargDistance){
		_TargDistance = TargDistance;
		_TargHeading.SetValue(_Chassis.GetCurrentHeading());
		_Chassis.ResetDistance();
		_CurrentDistance = _Chassis.GetCurrentDistance();
		_CurTask = TaskType.MoveAhead;
	}
	
	private void SetTaskMoveBack(double TargDistance){
		_TargDistance = -TargDistance;
		_TargHeading.SetValue(_Chassis.GetCurrentHeading());
		_Chassis.ResetDistance();
		_CurrentDistance = _Chassis.GetCurrentDistance();
		_CurTask = TaskType.MoveBack;
	}
	
	private void SetTaskRotate(double RotateDegrees){
		_TargDistance = 0.0;
		_TargHeading.SetValue(_Chassis.GetCurrentHeading() + RotateDegrees);
		_Chassis.ResetDistance();
		_CurrentDistance = _Chassis.GetCurrentDistance();
		_CurTask = TaskType.Rotate;
	}
	
	private void SetTaskRaiseClaw(){
		_TargDistance = 0.0;
		_TargHeading.SetValue(_Chassis.GetCurrentHeading());
		_Chassis.ResetDistance();
		_CurrentDistance = _Chassis.GetCurrentDistance();
		_CurTask = TaskType.RaiseClaw;
	}
	
	private void SetTaskLowerClaw(){
		_TargDistance = 0.0;
		_TargHeading.SetValue(_Chassis.GetCurrentHeading());
		_Chassis.ResetDistance();
		_CurrentDistance = _Chassis.GetCurrentDistance();
		_CurTask = TaskType.LowerClaw;
	}
	
	private void SetTaskPause(double PauseLength){
		_PauseExpire = System.currentTimeMillis() + (long)PauseLength;
		_CurTask = TaskType.Pause;
	}
	
	private void SetTaskDoNothing(){
		_CurTask = TaskType.DoNothing;
	}

	private void DoTaskPause(){
		if(System.currentTimeMillis() > _PauseExpire){
			TaskCompleted();
		}
	}
	
	
	private void DoTaskMoveAhead(){
		SmartDashboard.putString("Trace Routine", "DoTaskMoveAhead");

		double DistanceToGo = _TargDistance - _CurrentDistance;
		double DesiredSpeed = 0.0;
		if(DistanceToGo > 0.0){
			if(DistanceToGo > 120.0) { 
				DesiredSpeed = 0.85;
			}
			else if(DistanceToGo > 48){
				DesiredSpeed = 0.65;
			}
			else{
				DesiredSpeed = 0.55;
			}
			
			_Chassis.MoveChassis(DesiredSpeed, _TargHeading.GetValue());
		}
		else{
			_Chassis.StopChassis();
			TaskCompleted();
		}
	}
	
	private void DoTaskMoveBack(){
		SmartDashboard.putString("Trace Routine", "DoTaskMoveBack");
		double DistanceToGo = _TargDistance - _CurrentDistance;
		double DesiredSpeed = 0.0;
		
		if(DistanceToGo < 0.0){
			if(DistanceToGo < -120.0) { 
				DesiredSpeed = -0.85;
			}
			else if(DistanceToGo < -48.0){
				DesiredSpeed = -0.65;
			}
			else{
				DesiredSpeed = -0.55;
			}
			
			_Chassis.MoveChassis(DesiredSpeed, _TargHeading.GetValue());
		}
		else{
			_Chassis.StopChassis();
			TaskCompleted();
		}
	}
	
	private void DoTaskRotate(){
		SmartDashboard.putString("Trace Routine", "DoTaskRotate");
		SmartDashboard.putNumber("Rotate Targ", _TargHeading.GetValue());
		SmartDashboard.putNumber("Rotate Cur", _Chassis.GetCurrentHeading());
		if(_TargHeading.EssentiallyEquals(_Chassis.GetCurrentHeading())){
			_Chassis.StopChassis();
			TaskCompleted();
		}
		else{
			_Chassis.MoveChassis(0.0, _TargHeading.GetValue());
		}
	}
	
	private void DoTaskRaiseClaw(){
		if(_Claw.IsFullUp()){
			_Claw.ClawStop();
			TaskCompleted();
		}
		else{
			_Claw.ClawMoveUp();
		}
	}
	
	private void DoTaskLowerClaw(){
		if(_Claw.IsFullDown()){
			_Claw.ClawStop();
			TaskCompleted();
		}
		else{
			_Claw.ClawMoveDown();
		}
	}
	
}



/***************************************

public class GhostController {
	TankDrive _chassis;
	DigitalInput _FlagSensor = new DigitalInput(7);
	SmartDashboard dash = new SmartDashboard();
	RoboClaw _claw;
	boolean _moveForward = true;
	private AutoTasks _currentTask = AutoTasks.LookForFlag;
	private final long CLAW_TIME_MILLI = 600;
	private long ExpireTime;
	
	
	public GhostController(TankDrive inchassis, RoboClaw inclaw){
		_chassis = inchassis;
		_claw = inclaw;
	}
	
	public void AutonomousControl(){
		long time = System.currentTimeMillis();
		
		//_chassis.doNothing();
		
		switch(_currentTask){
		
			case SprintAcrossTheRoom:
				_chassis.MoveSlowly();
				break;
		
			case LookForFlag:
				_chassis.MoveSlowly();
				if(!_FlagSensor.get()){
					_chassis.doNothing();
					_currentTask = AutoTasks.GrabFlag;
					ExpireTime = time + CLAW_TIME_MILLI;
				}
				break;
			
			case GrabFlag:
				_claw.move(false, true);
				if(time >= ExpireTime){
					_claw.move(false, false);
					_currentTask = AutoTasks.DragFlagBack;
				}
				break;
			
			case DragFlagBack:
				_chassis.MoveBackSlowly();
				break;
				
			default:
				
				break;
		}
		
		
		/ *
		if(_moveForward){
			_chassis.MoveSlowly();
			if(!_FlagSensor.get()){
				_moveForward = false;
				_claw.move(false, true);
			}
		}
		else{
			_chassis.doNothing();
		}
		dash.putBoolean("FlagSensor", _FlagSensor.get());
		//_chassis.doNothing();
		 * /
	}
	
	public void Reset(){
		//_moveForward = true;
		_currentTask = AutoTasks.LookForFlag;
		_chassis.Reset();
		//_chassis._gyro.calibrate();
	}
}

**************************/
