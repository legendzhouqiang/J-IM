import React from 'react';
// import { Pagination } from 'antd';
import { connect } from 'dva';
import styles from './index.less';


class QAPage extends React.Component {

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
        <iframe src='https://www.oschina.net/question/tag/t-io' style={{ width: "100%", height: "830px", border:"none" }} frameborder="0" border="0" marginwidth="0" marginheight="0"  allowtransparency="yes"></iframe>
      </div>
    );
  }

}

QAPage.propTypes = {};

export default connect()(QAPage);
