import request from '../utils/request';
import basePath from '../utils/basePath';
// import qs from 'qs';

export async function casePage(pageNumber = 1, pageSize = 10) {
  return request({
    method: 'get',
    url: `${basePath}/case/page?pageNumber=${pageNumber}&pageSize=${pageSize}`,
  });
}
