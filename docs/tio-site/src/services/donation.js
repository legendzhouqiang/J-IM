import request from '../utils/request';
import basePath from '../utils/basePath';
// import qs from 'qs';


export async function totalAmount() {
  return request({
    method: 'get',
    url: `${basePath}/donate/totalAmount`,
  });
}

export async function donationPage(pageNumber = 1, pageSize = 10) {
  return request({
    method: 'get',
    url: `${basePath}/donate/page?pageNumber=${pageNumber}&pageSize=${pageSize}`,
  });
}







