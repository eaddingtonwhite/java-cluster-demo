# specify the provider and access details
provider "aws" {
  region = "${var.aws_region}"
}

resource "aws_elb" "elb" {
  name = "dist-keys-elb"

  # The same availability zone as our instances
  availability_zones = ["${split(",", var.availability_zones)}"]

  listener {
    instance_port     = 8090
    instance_protocol = "http"
    lb_port           = 80
    lb_protocol       = "http"
  }

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 3
    target              = "tcp:8090"
    interval            = 30
  }
}

resource "aws_autoscaling_group" "dist-keys-asg" {
  availability_zones   = ["${split(",", var.availability_zones)}"]
  name                 = "dist-keys-asg"
  max_size             = "${var.asg_max}"
  min_size             = "${var.asg_min}"
  desired_capacity     = "${var.asg_desired}"
  force_delete         = true
  launch_configuration = "${aws_launch_configuration.dist-keys-lc.name}"
  load_balancers       = ["${aws_elb.elb.name}"]
}

resource "aws_launch_configuration" "dist-keys-lc" {
  name          = "distkeys-launch-config"
  image_id      = "${lookup(var.aws_amis, var.aws_region)}"
  instance_type = "${var.instance_type}"

  # Security group
  security_groups = ["${aws_security_group.default.id}"]
  user_data       = "${file("userdata.sh")}"
  key_name        = "${var.key_name}"
}

# Our default security group to access
# the instances over SSH and HTTP
resource "aws_security_group" "default" {
  name        = "dist-keys-sg"

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP access from anywhere
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # Gossip access from inside vpc
  ingress {
    from_port   = 4801
    to_port     = 4801
    protocol    = "tcp"
    cidr_blocks = ["172.31.0.0/16"]
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
