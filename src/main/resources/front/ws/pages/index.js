import PageContainer from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"
import { useState } from "react";

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

function OneElementPreview() {
  return (<div className={styles.card} id="one_element_preview"></div>);
}



async function getAllCertificates() {
  return [{ t: 1, q: 2 }]
}
