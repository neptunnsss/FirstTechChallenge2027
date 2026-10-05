package org.firstinspires.ftc.teamcode.pedro.autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

// TODO: добавь самонаведение из TurretNav
@Autonomous(name = "firstauto", group = "Auto")
public class firstauto extends LinearOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private Follower follower;

    public enum pathState {
        //TODO: Добавить состояния
        STATE_1,
        STATE_2,
        STATE_3,
    }
    public pathState StatePath = pathState.STATE_1;

    private final Pose[] poses = {
            //TODO: Добавить позы
            new Pose(0, 0, 0),
            new Pose(0, 0, 0),
            new Pose(0, 0, 0)
    };

    // Ссылка на цепь путей PedroPathing
    private Object pathChain;

    @Override
    public void runOpMode() {
        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();

        waitForStart();

        while (opModeIsActive()) {
            StatePathUpdate();
            displayTelemetry();
        }
    }

    private void initializeHardware() {
        // Инициализация сабсистем
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);

        // TODO: Инициализировать follower после настройки PedroPathing Constants
        // follower = Constants.createFollower(hardwareMap);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Робот активен");
        telemetry.addData("Текущее состояние пути", StatePath);
        telemetry.addData("", "");

        if (drive != null) {
            telemetry.addData("FL мощность", "%.2f", drive.fLeftMotor.getPower());
            telemetry.addData("FR мощность", "%.2f", drive.fRightMotor.getPower());
            telemetry.addData("BL мощность", "%.2f", drive.bLeftMotor.getPower());
            telemetry.addData("BR мощность", "%.2f", drive.bRightMotor.getPower());
        }

        telemetry.update();
    }

    private void Outtake(boolean State) {
        // TODO: Добавить логику для сервопривода-толкателя, если он появится в конструкции.
        double outTakePower = 0;
        if (State) {
            outTakePower = 0.8;
        }
        outtake.setPower(outTakePower);
    }

    private void Intake(int State) {
        // TODO: Подумать над тем, чтобы сделать включение интейка по одной кнопке (Toggle on/off)
        // TODO: Добавить управление сервоприводом для опускания интейка
        double intakePower = 0;
        switch (State) {
            case 1:
                intakePower = 0.8;
                break;
            case 2:
                intakePower = -0.8;
                break;
            default:
                intakePower = 0;
        }
        intake.setPower(intakePower);
    }

    private void buildPath(int poseIndex) {
        // TODO: Вернуть создание пути после настройки PedroPathing в проекте
//         pathChain = follower.pathBuilder()
//                 .addPath(new BezierLine(poses[poseIndex], poses[poseIndex+1]))
//                 .setLinearHeadingInterpolation(poses[poseIndex].getHeading(), poses[poseIndex+1].getHeading())
//                 .build();
    }

    private void StatePathUpdate() {
        switch (StatePath) {
            case STATE_1:
                buildPath(0);
                Intake(0);
                Outtake(false);
                break;
            case STATE_2:
                Intake(1);
                break;
            case STATE_3:
                Intake(0);
                Outtake(true);
                break;
        }
    }
}