package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@SuppressWarnings("unused")
public class IntakeSubsystem {
    private final DcMotor intakeMotor1;
    private final DcMotor intakeMotor2;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor1 = hardwareMap.get(DcMotor.class, "IntakeMotor");
        
        intakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        intakeMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor2 = hardwareMap.get(DcMotor.class, "IntakeMotor");

        intakeMotor2.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double power) {
        intakeMotor1.setPower(power);
        intakeMotor2.setPower(power);
    }
}