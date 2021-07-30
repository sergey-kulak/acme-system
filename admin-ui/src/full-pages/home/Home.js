import { connect } from "react-redux";

function Home({ user }) {
    return (
        <div className="main-content">
            <div className="main-content-title">
                Welcome, {user.sub}
            </div>
        </div>
    )
}

const mapStateToProps = ({ auth }) => {
    return { user: auth.user };
};

export default connect(mapStateToProps,
    dispatch => ({})
)(Home);