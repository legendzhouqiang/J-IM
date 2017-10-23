import React from 'react';
import { Pagination } from 'antd';
import { connect } from 'dva';
import styles from './index.less';
import CaseItem from './CaseItem';
import { casePage } from '../../services/case';

class CasePage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      pageNumber: 1,
      pageSize: 4,
      totalRow: 100,
      list: [],
    };
  }
  componentWillMount() {
    this.queryCasePage(this.state.pageNumber, this.state.pageSize);
  }
  async queryCasePage(page, pageSize) {
    const { data } = await casePage(page, pageSize);
    this.setState(data);
  }
  render() {
    return (
      <div className={styles.container}>
        <div className={styles.bcolor}>

        </div>
        <div className={styles.main}>
          {
          this.state.list.map((item) => {
            return <CaseItem key={item.id} {...item} />;
          })
          }
          <Pagination
            style={{ textAlign: 'right' }}
            showSizeChanger
            showQuickJumper
            current={this.state.pageNumber}
            pageSize={this.state.pageSize}
            total={this.state.totalRow}
            onChange={this.queryCasePage.bind(this)}
            onShowSizeChange={this.queryCasePage.bind(this)}
          />
        </div>
      </div>
    );
  }

}

CasePage.propTypes = {};

export default connect()(CasePage);
