package client.domain

import shared.model.Snake

trait StatusRenderer {
  def render(snk: Snake): Unit
}
