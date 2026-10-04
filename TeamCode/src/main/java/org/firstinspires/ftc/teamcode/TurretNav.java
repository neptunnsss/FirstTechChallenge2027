package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.openftc.apriltag.AprilTagDetection;
//TODO: Добавить получение реального изоюражения от камеры
public class TurretNav {
    private DcMotor turret;
    private double Kp;
    private double Kd;
    private double target;
    private double last_error;
    private double tolerance;
    private final double MAX_POWER = 0.7;
    private double power = 0;
    private final ElapsedTime timer = new ElapsedTime();;
    public void init(HardwareMap hardwareMap){
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setDirection(DcMotor.Direction.FORWARD);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


    }
    public void update(AprilTagDetection curlID){
        double time;
        timer.reset();
        if (curlID != null){
            turret.setPower(0);
            last_error = 0;
            return;
        }
        double error = target - curlID.ftcPose.bearing;
        double p = error * Kp;
        double d = (error - last_error) * Kd;
        if (Math.abs(error) < tolerance){
            power = 0;

        }
        else{
            power = Range.clip(p+d,MAX_POWER, -MAX_POWER);
        }
        turret.setPower(power);
        last_error = error;
        time = timer.seconds();


    }
    public void resetTimer(){
        timer.reset();
    }
}

