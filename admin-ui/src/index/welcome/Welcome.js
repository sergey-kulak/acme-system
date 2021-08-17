import { Col, Container, Row } from 'react-bootstrap';
import { Link } from "react-router-dom";
import './Welcome.css';


function Welcome() {
  return (
    <div className="welcome text-center min-vh-100">
      <Container>
        <h3 className="pb-3">
          Acme Admin Site
        </h3>
        <Row>
          <Col>
            <Link to="/signin" className="btn btn-primary w-100">Sign in</Link>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Link to="/signup" className="btn btn-primary w-100">Sign up</Link>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Welcome;
