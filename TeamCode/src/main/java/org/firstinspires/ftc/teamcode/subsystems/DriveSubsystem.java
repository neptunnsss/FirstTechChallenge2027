package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class DriveSubsystem {
    public DcMotor fLeftMotor;
    public DcMotor fRightMotor;
    public DcMotor bLeftMotor;
    public DcMotor bRightMotor;
    public IMU imu;

    public DriveSubsystem(HardwareMap hardwareMap) {
        fLeftMotor = hardwareMap.get(DcMotor.class, "FleftMotor");
        fRightMotor = hardwareMap.get(DcMotor.class, "FrightMotor");
        bLeftMotor = hardwareMap.get(DcMotor.class, "BleftMotor");
        bRightMotor = hardwareMap.get(DcMotor.class, "BrightMotor");

        imu = hardwareMap.get(IMU.class, "imu");
        
        // Настройка направления в зависимости от того, как физически установлен Control Hub
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        imu.initialize(parameters);

        // TODO: Настроить ориентацию моторов после сборки
        fLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        bLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        fRightMotor.setDirection(DcMotor.Direction.REVERSE);
        bRightMotor.setDirection(DcMotor.Direction.REVERSE);

        fLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void driveFieldCentric(double x, double y, double rx, double speedMultiplier) {
        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);

        double fLeftPower = ((rotY + rotX + rx) / denominator) * speedMultiplier;
        double bLeftPower = ((rotY - rotX + rx) / denominator) * speedMultiplier;
        double fRightPower = ((rotY - rotX - rx) / denominator) * speedMultiplier;
        double bRightPower = ((rotY + rotX - rx) / denominator) * speedMultiplier;

        fLeftMotor.setPower(fLeftPower);
        bLeftMotor.setPower(bLeftPower);
        fRightMotor.setPower(fRightPower);
        bRightMotor.setPower(bRightPower);
    }

    public void driveRobotCentric(double x, double y, double rx, double speedMultiplier) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double fLeftPower = ((y + x + rx) / denominator) * speedMultiplier;
        double bLeftPower = ((y - x + rx) / denominator) * speedMultiplier;
        double fRightPower = ((y - x - rx) / denominator) * speedMultiplier;
        double bRightPower = ((y + x - rx) / denominator) * speedMultiplier;

        fLeftMotor.setPower(fLeftPower);
        bLeftMotor.setPower(bLeftPower);
        fRightMotor.setPower(fRightPower);
        bRightMotor.setPower(bRightPower);
    }

    public void stop() {
        fLeftMotor.setPower(0);
        bLeftMotor.setPower(0);
        fRightMotor.setPower(0);
        bRightMotor.setPower(0);
    }

    public void resetYaw() {
        imu.resetYaw();
    }
}