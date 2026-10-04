package org.firstinspires.ftc.teamcode.pedro.autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeSubsystem;

//TODO: пофиксить ошибки с импортированием
//TODO: попробуй сделать подсистему для камеры и добавь самонавидение из TurretNav
//TODO: добавить состаяние с интейком и ауттейком
@Autonomous(name = "firstauto", group = "Auto")
public class firstauto  extends LinearOpMode {
    private DcMotor FleftMotor;
    private DcMotor FrightMotor;
    private DcMotor BleftMotor;
    private DcMotor BrightMotor;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private IMU imu;
    private Follower follower;

    public enum pathState{
        //TODO: Добавить состояния
        STATE_1,
        STATE_2,
        STATE_3,
    }
    public pathState StatePath;

    private final Pose[] poses = {
            //TODO: Добавить позы
            new Pose(0,0,0),
            new Pose(0,0,0),
            new Pose(0,0,0)
    };
    private PathChain pathChain;
    private boolean StateIntake = false;


    private int StateOuttake = 0;

    private boolean endgameRumbled = false;
    private boolean matchEndRumbled = false;
    @Override
    public void runOpMode() {

        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();


        waitForStart();


        while (opModeIsActive()) {

            displayTelemetry();

        }
    }



    private void initializeHardware() {
        FleftMotor = hardwareMap.get(DcMotor.class, "FleftMotor");
        FrightMotor = hardwareMap.get(DcMotor.class, "FrightMotor");
        BleftMotor = hardwareMap.get(DcMotor.class, "BleftMotor");
        BrightMotor = hardwareMap.get(DcMotor.class, "BrightMotor");

        // Инициализация сабсистем
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        // TODO: Настрой направления в зависимости от того, как физически установлен Control Hub на роботе
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);
        //TODO: настроить ориентацию моторов
        FleftMotor.setDirection(DcMotor.Direction.FORWARD);
        BleftMotor.setDirection(DcMotor.Direction.FORWARD);
        FrightMotor.setDirection(DcMotor.Direction.REVERSE);
        BrightMotor.setDirection(DcMotor.Direction.REVERSE);

        FleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Робот активен");
        telemetry.addData("", "");

        telemetry.addData("Левый джойстик", "Y: %.2f", gamepad1.left_stick_y);
        telemetry.addData("Левый джойстик", "X: %.2f", gamepad1.left_stick_x);
        telemetry.addData("Правый джойстик", "X: %.2f", gamepad1.right_stick_x);
        telemetry.addData("Правый джойстик", "Y: %.2f", gamepad1.right_stick_y);
        telemetry.addData("Медленный режим", gamepad1.left_stick_button);

        telemetry.addData("","");
        telemetry.addData("FL мощность", "%.2f", FleftMotor.getPower());
        telemetry.addData("FR мощность", "%.2f", FrightMotor.getPower());
        telemetry.addData("BL мощность", "%.2f", BleftMotor.getPower());
        telemetry.addData("BR мощность", "%.2f", BrightMotor.getPower());

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
    private void buildPath(int poseIndex){
    pathChain = follower.pathBuilder()
            .addPath(new BezierLine(poses[poseIndex],poses[poseIndex+1]))
            .setLinearHeadingInterpolation(poses[poseIndex].getHeading(),poses[poseIndex+1].getHeading())
            .build();

    }
    private void StatePathUpdate(){
        switch (StatePath){
            case STATE_1:
                buildPath(0);
                follower.followPath(pathChain, true);
                break;

        }
    }

}