terraform {
  backend "gcs" {
    bucket  = "terraform-state-0"
    path    = "gcp/terraform.tfstate"
  }
}
