package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "firstCentricteleop", group = "TeleOp")
public class firstCentricteleop extends LinearOpMode {

    private DcMotor FleftMotor;
    private DcMotor FrightMotor;
    private DcMotor BleftMotor;
    private DcMotor BrightMotor;
    private DcMotor IntakeMotor;
    private DcMotor OuttakeMotor1;
    private DcMotor OuttakeMotor2;
    private IMU imu;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();

        telemetry.addData("Статус", "Инициализация завершена");
        telemetry.addData("Управление", "Левый джойстик - движение относительно поля");
        telemetry.addData("Управление", "Правый джойстик X - поворот робота");
        telemetry.addData("Управление", "Left Stick Button - медленный режим");
        telemetry.addData("Калибровка", "Нажмите OPTIONS для сброса севера");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.options) {
                imu.resetYaw();
            }

            controlDriveFieldCentric();
            displayTelemetry();
            Intake();
            Outtake();
        }
    }

    private void initializeHardware() {
        FleftMotor = hardwareMap.get(DcMotor.class, "FleftMotor");
        FrightMotor = hardwareMap.get(DcMotor.class, "FrightMotor");
        BleftMotor = hardwareMap.get(DcMotor.class, "BleftMotor");
        BrightMotor = hardwareMap.get(DcMotor.class, "BrightMotor");
        IntakeMotor = hardwareMap.get(DcMotor.class, "IntakeMotor");
        OuttakeMotor1 = hardwareMap.get(DcMotor.class, "OuttakeMotor1");
        OuttakeMotor2 = hardwareMap.get(DcMotor.class, "OuttakeMotor2");

        imu = hardwareMap.get(IMU.class, "imu");
        // ВАЖНО: Настрой направления в зависимости от того, как физически установлен Control Hub на роботе
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);

        FleftMotor.setDirection(DcMotor.Direction.FORWARD);
        BleftMotor.setDirection(DcMotor.Direction.FORWARD);
        FrightMotor.setDirection(DcMotor.Direction.REVERSE);
        BrightMotor.setDirection(DcMotor.Direction.REVERSE);

        IntakeMotor.setDirection(DcMotor.Direction.FORWARD);
        OuttakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        OuttakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        FleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        OuttakeMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        OuttakeMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        IntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        OuttakeMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        OuttakeMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        OuttakeMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        OuttakeMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void controlDriveFieldCentric() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;

        double speedMultiplier = gamepad1.left_stick_button ? 0.5 : 0.8;

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);

        double FleftPower = ((rotY + rotX + rx) / denominator) * speedMultiplier;
        double BleftPower = ((rotY - rotX + rx) / denominator) * speedMultiplier;
        double FrightPower = ((rotY - rotX - rx) / denominator) * speedMultiplier;
        double BrightPower = ((rotY + rotX - rx) / denominator) * speedMultiplier;

        FleftMotor.setPower(FleftPower);
        BleftMotor.setPower(BleftPower);
        FrightMotor.setPower(FrightPower);
        BrightMotor.setPower(BrightPower);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Робот активен");
        telemetry.addData("Время работы", "%.1f сек", runtime.seconds());
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
        double outTakePower = 0;
        if (gamepad1.right_bumper) {
            outTakePower = 0.9;
        } else if (gamepad1.left_bumper) {
            outTakePower = 0.5;
        }
        OuttakeMotor1.setPower(outTakePower);
        OuttakeMotor2.setPower(outTakePower);
    }

    private void Intake() {
        double intakePower = 0;
        if (gamepad1.right_trigger > 0.1) {
            intakePower = gamepad1.right_trigger;
        } else if (gamepad1.left_trigger > 0.1) {
            intakePower = -gamepad1.left_trigger;
        }
        IntakeMotor.setPower(intakePower);
    }
}