output "security_group" {
  value = "${aws_security_group.default.id}"
}

output "launch_configuration" {
  value = "${aws_launch_configuration.dist-keys-lc.id}"
}

output "asg_name" {
  value = "${aws_autoscaling_group.dist-keys-asg.id}"
}

output "elb_name" {
  value = "${aws_elb.elb.dns_name}"
}
