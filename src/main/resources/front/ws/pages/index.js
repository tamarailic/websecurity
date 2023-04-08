import PageContainer from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"
import { useState } from "react";
import Image from "next/image";

export default function Home() {
  return (<PageContainer>
    <HomePage />
  </PageContainer>);
}

function HomePage() {
  const [selectedSection, setSelectedSection] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState({ search: null, type: null, my: false });

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
      <MainArea setSelectedItem={setSelectedItem} appliedFilters={appliedFilters} />
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

  function handleChange(event) {
    appliedFilters['search'] = event.target.value;
    setAppliedFilters(appliedFilters);
  }


  return (
    <div className={styles.filter_options}>
      <input id="" type="text" placeholder="Search..." onChange={handleChange} />

    </div>
  );
}

function MainArea({ setSelectedItem, appliedFilters }) {
  const certificates = getAllCertificates();
  return (
    <div id="main_area">
      <table className={styles.main_table}>
        <thead>
          <tr>

          </tr>
        </thead>


      </table>
    </div>
  );
}

function OneElementPreview({selectedItem}) {
  // return (<div className={styles.card} id="one_element_preview"></div>);
  return (<div className={`${styles.card} ${styles.preview}`} id="one_element_preview">
    <h2>Certificate</h2>
    <ul className={styles.certInfo}>
      {/* <li>Serial number: <span>{selectedItem.serialNumber}</span></li>
      <li>Owner: <span>{selectedItem.owner}</span></li>
      <li>Issuer: <span>{selectedItem.issuer}</span></li>
      <li>Valid till: <span>{selectedItem.notAfter}</span></li>
      <li>Type: <span>{selectedItem.type}</span></li> */}
      <li><span className={styles.certInfoLabel}>Serial number: </span><span>32323232</span></li>
      <li><span className={styles.certInfoLabel}>Owner: </span><span>Marko</span></li>
      <li><span className={styles.certInfoLabel}>Issuer: </span><span>Luka</span></li>
      <li><span className={styles.certInfoLabel}>Valid till: </span><span>22.09.2023</span></li>
      <li><span className={styles.certInfoLabel}>Type: </span><span>INTERMEDIATE</span></li>
    </ul>
    <hr/>
    {/* <RequestCertificate canRequest={selectedItem.type != 'END'}/> */}
    {true && <RequestCertificate/> }
    {true && <InvalidateButton/>}
    <DownloadButton/>
  </div>);
}

function RequestCertificate(){
  const possibleCertificateTypes = ['INTERMEDIATE', 'END']

  return (<div>
      <p className={styles.certInfoLabel}>Request certificate</p>
      <div className={styles.requestCert}>
        <select className={styles.requestType} name="certType" id="certType">
        {possibleCertificateTypes.map(type => <option value={type}>{type}</option>)}
        </select>
        <button className={styles.btn}>Request</button>
      </div>
    </div>)

}

function InvalidateButton(){
  return <button className={styles.accentBtn}>Invalidate</button>
}

function DownloadButton(){
  return <div className={styles.accentBtn}>
          <a>Download</a>
          <Image src="/images/downloadCertificateIcon.png" width={24} height={24}></Image>
        </div>
 
}


async function getAllCertificates() {
  return [{ t: 1, q: 2 }]
}
