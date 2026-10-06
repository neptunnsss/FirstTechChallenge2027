package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.AprilTagSubsystem;
//TODO: Добавить получение реального изображения от limelight-a
//TODO: Если надо поменять на серво
public class TurretNav {
    private DcMotor turret;

//TODO: Добавь значения tolerance, Kp, Kd, MAX_POWER
    private double Kp;
    private double Kd;
    private double target;
    private double last_error;
    private double tolerance = 2;
    private final double MAX_POWER = 0.7;
    private double power = 0;
    private final ElapsedTime timer = new ElapsedTime();
    private AprilTagSubsystem aprilTagSubsystem;
    public void init(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setDirection(DcMotor.Direction.FORWARD);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        aprilTagSubsystem = new AprilTagSubsystem();
        aprilTagSubsystem.init(hardwareMap);

    }
    public void update(){
        aprilTagSubsystem.update();
        LLResult LLResult = aprilTagSubsystem.limelight3A.getLatestResult();
        double time;
        timer.reset();
        if (LLResult == null){
            turret.setPower(0);
            last_error = 0;
            return;
        }
        double Tx = LLResult.getTx();
        double error = target - Tx;
        double p = error * Kp;
        double d = (error - last_error) * Kd;
        if (Math.abs(error) < tolerance) {
            power = 0;
        } else {
            // ИСПРАВЛЕНИЕ: Range.clip(значение, минимум, максимум)
            power = Range.clip(p + d, -MAX_POWER, MAX_POWER);
        }
        turret.setPower(power);
        last_error = error;
        time = timer.seconds();


    }
    public void resetTimer(){
        timer.reset();
    }
}

