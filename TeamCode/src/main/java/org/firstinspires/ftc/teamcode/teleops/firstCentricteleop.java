package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.TurretNav;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

// TODO: ОБЩАЯ СТРУКТУРА РОБОТА ПОКА НЕИЗВЕСТНА. ПРЕДСТОИТ ДОРАБОТАТЬ ПОСЛЕ СБОРКИ:
// TODO: 1. Интейк: возможно, понадобится добавить сервопривод (Servo) для опускания/поднятия самого интейка.
// TODO: 2. Шутер (Outtake): проверить, нужен ли сервопривод-толкатель (feeder) для подачи элементов в маховик.
// TODO: 3. Шутер: для стабильной стрельбы лучше использовать PID-контроллер (RUN_USING_ENCODER и setVelocity) вместо обычной мощности (setPower).//
@TeleOp(name = "firstCentricteleop", group = "TeleOp")
public class firstCentricteleop extends LinearOpMode {
    private DcMotor FleftMotor;
    private DcMotor FrightMotor;
    private DcMotor BleftMotor;
    private DcMotor BrightMotor;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;

    private IMU imu;
    private final ElapsedTime runtime = new ElapsedTime();
//    private TurretNav turret;


    private boolean isFieldCentric = true;
    private boolean aButtonPressed = false;

    private boolean endgameRumbled = false;
    private boolean matchEndRumbled = false;
    @Override
    public void runOpMode() {
        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();
//        turret = new TurretNav();
//        turret.init(hardwareMap);
//        turret.resetTimer();

        telemetry.addData("Статус", "Инициализация завершена");
        telemetry.addData("Управление", "Левый джойстик - движение относительно поля");
        telemetry.addData("Управление", "Правый джойстик X - поворот робота");
        telemetry.addData("Управление", "Left Stick Button - медленный режим");
        telemetry.addData("Управление", "Кнопка A (Cross) - переключение Field/Driver Centric");
        telemetry.addData("Калибровка", "Нажмите OPTIONS для сброса севера (Field Centric)");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.options) {
                imu.resetYaw();
            }

            if (gamepad1.a && !aButtonPressed) {
                isFieldCentric = !isFieldCentric;
            }
            aButtonPressed = gamepad1.a;

            if (isFieldCentric) {
                controlDriveFieldCentric();
            } else {
                controlDriveRobotCentric();
            }

            handleMatchTimer();
            displayTelemetry();
            Intake();
            Outtake();
            //добавь turretnav только после тюнинга PD
//            turretNavingation();
        }
    }

    private void handleMatchTimer() {
        double time = runtime.seconds();

        if (time >= 90 && !endgameRumbled) {
            gamepad1.rumble(500);
            gamepad2.rumble(500);
            endgameRumbled = true;
        }

        if (time >= 110 && !matchEndRumbled) {
            gamepad1.rumbleBlips(3);
            gamepad2.rumbleBlips(3);
            matchEndRumbled = true;
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
        
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        imu.initialize(parameters);
        
        FleftMotor.setDirection(DcMotor.Direction.FORWARD);
        FrightMotor.setDirection(DcMotor.Direction.FORWARD);
        BleftMotor.setDirection(DcMotor.Direction.REVERSE);
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

    private void controlDriveFieldCentric() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;

        if (Math.abs(x) < 0.05 && Math.abs(y) < 0.05 && Math.abs(rx) < 0.05) {
            FleftMotor.setPower(0);
            BleftMotor.setPower(0);
            FrightMotor.setPower(0);
            BrightMotor.setPower(0);
            return;
        }

        double speedMultiplier = gamepad1.left_stick_button ? 0.5 : 0.8;

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        double FleftPower = ((rotY + rotX + rx)) * speedMultiplier;
        double BleftPower = ((rotY - rotX + rx)) * speedMultiplier;
        double FrightPower = ((rotY - rotX - rx)) * speedMultiplier;
        double BrightPower = ((rotY + rotX - rx) )* speedMultiplier;

        FleftMotor.setPower(FleftPower);
        BleftMotor.setPower(BleftPower);
        FrightMotor.setPower(FrightPower);
        BrightMotor.setPower(BrightPower);
    }

    private void controlDriveRobotCentric() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if (Math.abs(x) < 0.05 && Math.abs(y) < 0.05 && Math.abs(rx) < 0.05) {
            FleftMotor.setPower(0);
            BleftMotor.setPower(0);
            FrightMotor.setPower(0);
            BrightMotor.setPower(0);
            return;
        }

        double speedMultiplier = gamepad1.left_stick_button ? 0.5 : 0.8;

        double FleftPower = ((y + x + rx)) * speedMultiplier;
        double BleftPower = ((y - x + rx)) * speedMultiplier;
        double FrightPower = ((y - x - rx)) * speedMultiplier;
        double BrightPower = ((y + x - rx)) * speedMultiplier;

        FleftMotor.setPower(FleftPower);
        BleftMotor.setPower(BleftPower);
        FrightMotor.setPower(FrightPower);
        BrightMotor.setPower(BrightPower);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Робот активен");
        telemetry.addData("Время работы", "%.1f сек", runtime.seconds());
        telemetry.addData("Режим езды", isFieldCentric ? "FIELD CENTRIC" : "ROBOT CENTRIC");
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

    private void Outtake() {
        // TODO: Добавить логику для сервопривода-толкателя, если он появится в конструкции.
        double outTakePower = 0;
        if (gamepad1.right_bumper) {
            outTakePower = 0.8;
        }
        outtake.setPower(outTakePower);
    }

    private void Intake() {
        // TODO: Подумать над тем, чтобы сделать включение интейка по одной кнопке (Toggle on/off)
        // TODO: Добавить управление сервоприводом для опускания интейка
        double intakePower = 0;
        if (gamepad1.right_trigger > 0.1) {
            intakePower = gamepad1.right_trigger;
        } else if (gamepad1.left_trigger > 0.1) {
            intakePower = -gamepad1.left_trigger;
        }
        intakePower = intakePower * 0.8;
        intake.setPower(intakePower);
    }
//    private void turretNavingation(){
//        //TODO: Подключи сюда AprilTagSubsystem
//        turret.update(id);
//    }
}
