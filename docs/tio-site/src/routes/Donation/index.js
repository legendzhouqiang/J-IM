import React from 'react';
// import { Pagination } from 'antd';
import { connect } from 'dva';
import styles from './index.less';


class DonationPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      pageNumber: 1,
      pageSize: 10,
      totalRow: 100,
      list: [],
    };
  }
  componentWillMount() {

  }

  render() {
    return (
      <div className={styles.container}>
      123
      </div>
    );
  }

}

DonationPage.propTypes = {};

export default connect()(DonationPage);
