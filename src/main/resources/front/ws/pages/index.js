import { useState } from "react";
import useSWR from 'swr'
import PageContainer from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"
import { backUrl } from "@/components/pageContainer";
import Spinner from "@/components/spinner";
import Error from "@/components/error";

const fetcher = (...args) => fetch(...args).then(res => res.json());

// Just mocked data -> should be replaced with data from JWT
const userId = '6428997eb6dc0fe0233cd979';
const username = 'Test';

export default function Home() {
  return (<PageContainer>
    <HomePage />
  </PageContainer>);
}

function HomePage() {
  const [selectedSection, setSelectedSection] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState({ search: null, type: null, onlyMy: false });

  // Na klik nekog reda promeni se selectedItem (u certificate ili request zavisi koji je prikaz)
  // na pocetku je null
  console.log(selectedItem);

  function showSection(i) {
    setSelectedSection(i);
  }

  return (
    <section className={styles.main_grid}>
      <AllElementsTable selectedSection={selectedSection} showSection={showSection} setSelectedItem={setSelectedItem} appliedFilters={appliedFilters} setAppliedFilters={setAppliedFilters} />
      <OneElementPreview selectedItem={selectedItem} />
    </section >);
}

function AllElementsTable({ selectedSection, showSection, setSelectedItem, appliedFilters, setAppliedFilters }) {
  return (
    <div className={styles.card} id="all_elements_table">
      <ToggleOptions selectedSection={selectedSection} showSection={showSection} />
      <FilterOptions appliedFilters={appliedFilters} setAppliedFilters={setAppliedFilters} />
      <MainArea selectedSection={selectedSection} appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
    </div>
  );
}

function ToggleOptions({ selectedSection, showSection }) {
  return (
    <div className={styles.toggle_options}>
      <p onClick={() => showSection(0)} className={selectedSection == 0 ? styles.selected : ''}>All certificates</p>
      <div className={styles.separator}></div>
      <p onClick={() => showSection(1)} className={selectedSection == 1 ? styles.selected : ''}>My requests</p>
      <div className={styles.separator}></div>
      <p onClick={() => showSection(2)} className={selectedSection == 2 ? styles.selected : ''}>For signing</p>
    </div>
  );
}

function FilterOptions({ appliedFilters, setAppliedFilters }) {

  function handleSearchChange(event) {
    const newAppliedFilters = { search: event.target.value, type: appliedFilters['type'], onlyMy: appliedFilters['onlyMy'] };
    setAppliedFilters(newAppliedFilters);
  }

  function handleTypeChange(event) {
    const newAppliedFilters = { search: appliedFilters['search'], type: event.target.value, onlyMy: appliedFilters['onlyMy'] };
    setAppliedFilters(newAppliedFilters);
  }

  function handleCheckboxChange(event) {
    const newAppliedFilters = { search: appliedFilters['search'], type: appliedFilters['type'], onlyMy: event.target.checked };
    setAppliedFilters(newAppliedFilters);
  }

  return (
    <div className={styles.filter_options}>
      <input id="search" name="search" type="text" placeholder="Search..." onChange={handleSearchChange} />
      <select id="type" onChange={handleTypeChange}>
        <option value="ALL">ALL</option>
        <option value="ROOT">ROOT</option>
        <option value="INTERMEDIATE">INTERMEDIATE</option>
        <option value="END">END</option>
      </select>
      <div>
        <label htmlFor="onlyMy">Only my:</label>
        <input id="onlyMy" name="onlyMy" type="checkbox" onChange={handleCheckboxChange} />
      </div>
    </div>
  );
}

function MainArea({ selectedSection, appliedFilters, setSelectedItem }) {
  let tableToRender = null;
  if (selectedSection == 0) {
    tableToRender = <AllCertificates appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  } else if (selectedSection == 1) {
    tableToRender = <MyRequests appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  } else if (selectedSection == 2) {
    tableToRender = <ForSigning appliedFilters={appliedFilters} setSelectedItem={setSelectedItem} />
  }
  return (
    <div id="main_area">
      {tableToRender}
    </div>
  );
}

function AllCertificates({ appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { certificates, isLoading, isError } = getAllCertificates();

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const certificatesData = filterResults(certificates.content, appliedFilters);

  if (certificatesData == null || certificatesData.length == 0) return null;

  const columnNames = Object.keys(certificatesData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>valid</th>
          {columnNames.filter(column => ['serialNumber', 'owner', 'issuer', 'type', 'notBefore', 'notAfter'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {certificatesData.map(item => <tr onClick={() => handleRowClick(item)} key={item['serialNumber']}><td>{item['valid'] ? <div className={styles.validCircle}></div> : <div className={styles.invalidCircle}></div>}</td>{Object.keys(item).filter(column => ['serialNumber', 'owner', 'issuer', 'type', 'notBefore', 'notAfter'].includes(column)).map(itemKey => <td key={`${item['serialNumber']}-${itemKey}`}>{item[itemKey]}</td>)}</tr>)}
      </tbody>
    </table>
  );

}

function MyRequests({ appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { requests, isLoading, isError } = getAllUserRequests(userId);

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const requestsData = filterResults(requests, appliedFilters);

  if (requestsData == null || requestsData.length == 0) return null;

  const columnNames = Object.keys(requestsData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>status</th>
          {columnNames.filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {requestsData.map(item => <tr onClick={() => handleRowClick(item)} key={item['requestId']}><td>{item['status'] == 'APPROVED' ? <div className={styles.validCircle}></div> : item['status'] == 'DENIED' ? <div className={styles.invalidCircle}></div> : <div className={styles.pendingCircle}></div>}</td>{Object.keys(item).filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(itemKey => <td key={`${item['subjectId']}-${itemKey}`}>{item[itemKey]}</td>)}</tr>)}
      </tbody>
    </table>
  );
}

function ForSigning({ appliedFilters, setSelectedItem }) {
  function handleRowClick(certificate) {
    setSelectedItem(certificate);
  }

  const { requests, isLoading, isError } = getAllRequestsToReview(userId);

  if (isLoading) return <Spinner />
  if (isError) return <Error />

  const requestsData = filterResults(requests, appliedFilters);

  if (requestsData == null || requestsData.length == 0) return null;

  const columnNames = Object.keys(requestsData[0]);

  return (
    <table className={styles.main_table}>
      <thead>
        <tr>
          <th>status</th>
          {columnNames.filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(column => <th key={column}>{column}</th>)}
        </tr>
      </thead>
      <tbody>
        {requestsData.map(item => <tr onClick={() => handleRowClick(item)} key={item['requestId']}><td>{item['status'] == 'APPROVED' ? <div className={styles.validCircle}></div> : item['status'] == 'DENIED' ? <div className={styles.invalidCircle}></div> : <div className={styles.pendingCircle}></div>}</td>{Object.keys(item).filter(column => ['requestId', 'subjectId', 'issuerCertificateId', 'requestedDate', 'certificateType'].includes(column)).map(itemKey => <td key={`${item['subjectId']}-${itemKey}`}>{item[itemKey]}</td>)}</tr>)}
      </tbody>
    </table>
  );
}

function getAllCertificates() {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all`, fetcher)

  return {
    certificates: data,
    isLoading,
    isError: error
  }
}

function getAllUserRequests(userId) {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all-certificate-requests/${userId}`, fetcher)

  return {
    requests: data,
    isLoading,
    isError: error
  }
}

function getAllRequestsToReview(userId) {
  const { data, error, isLoading } = useSWR(`${backUrl}/api/certificate/all-requests-to-review/${userId}`, fetcher)

  return {
    requests: data,
    isLoading,
    isError: error
  }
}

function filterResults(data, filters) {
  let dataThatFulfillsFilters = JSON.parse(JSON.stringify(data))
  if (filters['search'] != null && filters['search'] != '') {
    for (let item of dataThatFulfillsFilters) {
      let fulfillsFilters = false;
      for (let key in item) {
        if (String(item[key]).toLowerCase().includes(filters['search'].toLowerCase())) {
          fulfillsFilters = true
          break;
        }
      }
      if (!fulfillsFilters) {
        dataThatFulfillsFilters.splice(dataThatFulfillsFilters.indexOf(item), 1);
      }
    }
  }
  if (filters['type'] != null && filters['type'] != 'ALL') {
    dataThatFulfillsFilters = dataThatFulfillsFilters.filter(item => item['type'] == filters['type'] || item['certificateType'] == filters['type']);
  }
  if (filters['onlyMy']) {
    dataThatFulfillsFilters = dataThatFulfillsFilters.filter(item => item['owner'] == username || item['subjectId'] == username);
  }
  return dataThatFulfillsFilters;
}

function OneElementPreview() {
  return (<div className={styles.card} id="one_element_preview"></div>);
}
