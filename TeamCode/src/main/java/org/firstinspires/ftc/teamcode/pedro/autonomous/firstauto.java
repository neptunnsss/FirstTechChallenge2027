package org.firstinspires.ftc.teamcode.pedro.autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

// TODO: попробуй сделать подсистему для камеры и добавь самонаведение из TurretNav
@Autonomous(name = "firstauto", group = "Auto")
public class firstauto extends LinearOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private Follower follower;

    public enum PathState {
        STATE_1,
        STATE_2,
        STATE_3
    }
    public PathState statePath = PathState.STATE_1;

    private final Pose[] poses = {
            new Pose(0, 0, 0),
            new Pose(0, 0, 0),
            new Pose(0, 0, 0)
    };

    @Override
    public void runOpMode() {
        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();

        waitForStart();

        while (opModeIsActive()) {
            statePathUpdate();
            displayTelemetry();
        }
    }

    private void initializeHardware() {
        // Инициализация подсистем
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);

        // TODO: Инициализировать follower после настройки PedroPathing Constants
        // follower = Constants.createFollower(hardwareMap);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Автономный период активен");
        telemetry.addData("Текущее состояние пути", statePath);

        if (drive != null) {
            telemetry.addData("FL мощность", "%.2f", drive.fLeftMotor.getPower());
            telemetry.addData("FR мощность", "%.2f", drive.fRightMotor.getPower());
            telemetry.addData("BL мощность", "%.2f", drive.bLeftMotor.getPower());
            telemetry.addData("BR мощность", "%.2f", drive.bRightMotor.getPower());
        }

        if (follower != null) {
            telemetry.addData("PedroPathing Mode", follower.mode());
        }
        telemetry.update();
    }

    private void setOuttake(boolean active) {
        double outTakePower = active ? 0.8 : 0.0;
        outtake.setPower(outTakePower);
    }

    private void setIntake(int mode) {
        double intakePower;
        switch (mode) {
            case 1:
                intakePower = 0.8;
                break;
            case 2:
                intakePower = -0.8;
                break;
            default:
                intakePower = 0.0;
                break;
        }
        intake.setPower(intakePower);
    }

    private void statePathUpdate() {
        switch (statePath) {
            case STATE_1:
                // TODO: Добавить логику движения и управления интейком/ауттейком для состояния 1
                setIntake(0);
                setOuttake(false);
                break;

            case STATE_2:
                setIntake(1); // Включение интейка
                break;

            case STATE_3:
                setIntake(0);
                setOuttake(true); // Включение ауттейка (шутера)
                break;
        }
    }
}