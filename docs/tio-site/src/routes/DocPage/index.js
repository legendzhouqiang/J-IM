import React from 'react';
// import { Pagination } from 'antd';
import { connect } from 'dva';
import styles from './index.less';


class DocPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {

    };
  }
  componentWillMount() {

  }

  render() {
    return (
      <div className={styles.container} >
        <iframe src='/doc/index.html' style={{ width: "100%", height: "760px", border:"none" }} frameborder="0" border="0" marginwidth="0" marginheight="0"  allowtransparency="yes"></iframe>
      </div>
    );
  }

}

DocPage.propTypes = {};

export default connect()(DocPage);
